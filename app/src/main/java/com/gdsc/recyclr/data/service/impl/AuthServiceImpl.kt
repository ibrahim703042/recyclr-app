package com.gdsc.recyclr.data.service.impl

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.gdsc.recyclr.data.model.UserDto
import com.gdsc.recyclr.data.service.AuthService
import com.gdsc.recyclr.util.AppLogger
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthServiceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthService {

    override fun peekCurrentUser(): UserDto? = firebaseAuth.currentUser?.toDto()

    override suspend fun getCurrentUser(): UserDto? = peekCurrentUser()

    override suspend fun signUpWithEmailAndPassword(
        name: String,
        email: String,
        password: String
    ): Result<Boolean> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            firebaseAuth.currentUser?.updateProfile(
                com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
            )?.await()
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
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(true)
        } catch (e: Exception) {
            AppLogger.w("signInWithEmailAndPassword", e)
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogleIdToken(idToken: String): Result<Boolean> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).await()
            Result.success(true)
        } catch (e: Exception) {
            AppLogger.w("signInWithGoogleIdToken", e)
            Result.failure(e)
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
        }.onFailure {
            AppLogger.e("startPhoneVerification — échec configuration", it)
            callbacks.onVerificationFailed(
                FirebaseException("Phone Auth indisponible : ${it.message}")
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
            firebaseAuth.signInWithCredential(credential).await()
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
            trySend(auth.currentUser == null)
        }
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
