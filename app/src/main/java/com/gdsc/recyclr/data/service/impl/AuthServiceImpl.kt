package com.gdsc.recyclr.data.service.impl

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.firestore.FirebaseFirestore
import com.gdsc.recyclr.data.model.UserDto
import com.gdsc.recyclr.data.service.AuthService
import com.gdsc.recyclr.util.AppLogger
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthServiceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val firestore: FirebaseFirestore
) : AuthService {

    override fun peekCurrentUser(): UserDto? {
        val user = firebaseAuth.currentUser ?: return null
        // We can't really do a sync call to Firestore here easily without blocking.
        // For peek, we'll return what we have in Auth and use default role.
        return user.toDto()
    }

    override suspend fun getCurrentUser(): UserDto? {
        val user = firebaseAuth.currentUser ?: return null
        val dto = user.toDto()
        return try {
            val doc = firestore.collection("users").document(user.uid).get().await()
            if (doc.exists()) {
                dto.copy(role = doc.getString("role") ?: "USER")
            } else {
                // Initialize user doc if missing
                firestore.collection("users").document(user.uid).set(dto).await()
                dto
            }
        } catch (e: Exception) {
            dto
        }
    }

    override suspend fun signUpWithEmailAndPassword(
        name: String,
        email: String,
        password: String
    ): Result<Boolean> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("User creation failed")
            
            user.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
            ).await()
            
            ensureUserDocumentExists(user)
            Result.success(true)
        } catch (e: Exception) {
            AppLogger.w("signUpWithEmailAndPassword", e)
            Result.failure(e)
        }
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<Boolean> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { ensureUserDocumentExists(it) }
            Result.success(true)
        } catch (e: Exception) {
            AppLogger.w("signInWithEmailAndPassword", e)
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogleIdToken(idToken: String): Result<Boolean> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            result.user?.let { ensureUserDocumentExists(it) }
            Result.success(true)
        } catch (e: Exception) {
            AppLogger.w("signInWithGoogleIdToken", e)
            Result.failure(e)
        }
    }

    private suspend fun ensureUserDocumentExists(user: FirebaseUser) {
        try {
            val userDoc = firestore.collection("users").document(user.uid).get().await()
            if (!userDoc.exists()) {
                val dto = user.toDto()
                firestore.collection("users").document(user.uid).set(dto).await()
            }
            
            val impactDoc = firestore.collection("user_impact").document(user.uid).get().await()
            if (!impactDoc.exists()) {
                val impactDto = com.gdsc.recyclr.data.model.UserImpactDto(
                    userId = user.uid,
                    displayName = user.displayName ?: user.email?.substringBefore('@') ?: "Green Hero",
                    role = userDoc.getString("role") ?: "USER"
                )
                firestore.collection("user_impact").document(user.uid).set(impactDto).await()
            }
        } catch (e: Exception) {
            AppLogger.w("ensureUserDocumentExists", e)
        }
    }

    override fun startPhoneVerification(
        activity: Activity,
        phoneNumberE164: String,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        runCatching {
            val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumberE164)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }.onFailure { error ->
            AppLogger.e("startPhoneVerification — échec configuration", error)
            callbacks.onVerificationFailed(
                FirebaseAuthException(
                    "ERROR_INTERNAL_ERROR",
                    "Phone Auth indisponible : ${error.message}",
                ),
            )
        }
    }

    override suspend fun signInWithPhoneSmsCode(verificationId: String, smsCode: String): Result<Boolean> {
        val credential = PhoneAuthProvider.getCredential(verificationId, smsCode)
        return signInWithAuthCredential(credential)
    }

    override suspend fun signInWithPhoneCredential(credential: PhoneAuthCredential): Result<Boolean> =
        signInWithAuthCredential(credential)

    private suspend fun signInWithAuthCredential(credential: AuthCredential): Result<Boolean> {
        return try {
            val result = firebaseAuth.signInWithCredential(credential).await()
            result.user?.let { ensureUserDocumentExists(it) }
            Result.success(true)
        } catch (e: Exception) {
            AppLogger.w("signInWithCredential", e)
            Result.failure(e)
        }
    }

    override suspend fun sendEmailVerification(): Result<Boolean> {
        return try {
            firebaseAuth.currentUser?.sendEmailVerification()?.await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun reloadUser(): Result<Boolean> {
        return try {
            firebaseAuth.currentUser?.reload()?.await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Boolean> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfilePhoto(photoUrl: String): Result<Boolean> {
        return try {
            val user = firebaseAuth.currentUser ?: throw Exception("User not signed in")
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(photoUrl))
                .build()
            user.updateProfile(profileUpdates).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateDisplayName(displayName: String): Result<Boolean> {
        return try {
            val trimmed = displayName.trim()
            if (trimmed.isBlank()) throw IllegalArgumentException("Display name cannot be empty")
            val user = firebaseAuth.currentUser ?: throw Exception("User not signed in")
            user.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(trimmed)
                    .build(),
            ).await()
            firestore.collection("users").document(user.uid)
                .update(mapOf("displayName" to trimmed))
                .await()
            runCatching {
                firestore.collection("user_impact").document(user.uid)
                    .update(mapOf("displayName" to trimmed))
                    .await()
            }
            Result.success(true)
        } catch (e: Exception) {
            AppLogger.w("updateDisplayName", e)
            Result.failure(e)
        }
    }

    override fun getAccountCreationMillis(): Long? =
        firebaseAuth.currentUser?.metadata?.creationTimestamp

    override suspend fun uploadProfilePhoto(userId: String, bitmap: Bitmap): Result<String> {
        return try {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
            val data = baos.toByteArray()

            val ref = storage.reference.child("users/$userId/profile.jpg")
            ref.putBytes(data).await()
            val url = ref.downloadUrl.await().toString()
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }

    override suspend fun revokeAccess(): Result<Boolean> {
        return try {
            firebaseAuth.currentUser?.delete()?.await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeAuthState(): Flow<Boolean> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser == null).isSuccess
        }
        trySend(firebaseAuth.currentUser == null)
        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }

    private fun FirebaseUser.toDto(): UserDto {
        val providers = providerData.mapNotNull { it.providerId }
            .distinct()
            .joinToString(", ")
            .ifBlank { null }
        return UserDto(
            uid = uid,
            email = email,
            displayName = displayName,
            phoneNumber = phoneNumber,
            photoUrl = photoUrl?.toString(),
            isEmailVerified = isEmailVerified,
            authProvidersSummary = providers
        )
    }
}
