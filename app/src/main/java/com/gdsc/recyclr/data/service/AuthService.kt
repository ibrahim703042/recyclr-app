package com.gdsc.recyclr.data.service

import android.app.Activity
import com.gdsc.recyclr.data.model.UserDto
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.Flow

/**
 * Authentification Firebase — email, Google, téléphone.
 */
interface AuthService {

    /** Snapshot synchrone depuis FirebaseAuth (évite runBlocking). */
    fun peekCurrentUser(): UserDto?

    suspend fun getCurrentUser(): UserDto?

    suspend fun signUpWithEmailAndPassword(name: String, email: String, password: String): Result<Boolean>

    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<Boolean>

    suspend fun signInWithGoogleIdToken(idToken: String): Result<Boolean>

    fun startPhoneVerification(
        activity: Activity,
        phoneNumberE164: String,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    )

    suspend fun signInWithPhoneSmsCode(verificationId: String, smsCode: String): Result<Boolean>

    suspend fun signInWithPhoneCredential(credential: PhoneAuthCredential): Result<Boolean>

    suspend fun sendEmailVerification(): Result<Boolean>

    suspend fun reloadUser(): Result<Boolean>

    suspend fun sendPasswordResetEmail(email: String): Result<Boolean>

    fun signOut()

    suspend fun revokeAccess(): Result<Boolean>

    fun observeAuthState(): Flow<Boolean>
}
