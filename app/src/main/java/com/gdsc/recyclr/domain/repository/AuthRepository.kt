package com.gdsc.recyclr.domain.repository

import android.graphics.Bitmap
import android.app.Activity
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.User

typealias SignUpResponse = Response<Boolean>
typealias SendEmailVerificationResponse = Response<Boolean>
typealias SignInResponse = Response<Boolean>
typealias ReloadUserResponse = Response<Boolean>
typealias SendPasswordResetEmailResponse = Response<Boolean>
typealias RevokeAccessResponse = Response<Boolean>
typealias AuthStateResponse = StateFlow<Boolean>

interface AuthRepository {
    val currentUser: User?

    suspend fun getCurrentUser(): User?

    suspend fun signUpWithEmailAndPassword(name: String, email: String, password: String): SignUpResponse

    suspend fun sendEmailVerification(): SendEmailVerificationResponse

    suspend fun signInWithEmailAndPassword(email: String, password: String): SignInResponse

    suspend fun signInWithGoogleIdToken(idToken: String): SignInResponse

    fun startPhoneVerification(
        activity: Activity,
        phoneNumberE164: String,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    )

    suspend fun signInWithPhoneSmsCode(verificationId: String, smsCode: String): SignInResponse

    suspend fun signInWithPhoneCredential(credential: PhoneAuthCredential): SignInResponse

    suspend fun reloadUser(): ReloadUserResponse

    suspend fun sendPasswordResetEmail(email: String): SendPasswordResetEmailResponse

    suspend fun updateProfilePhoto(photoUrl: String): Response<Boolean>

    suspend fun uploadProfilePhoto(userId: String, bitmap: Bitmap): Response<String>

    fun signOut()

    suspend fun revokeAccess(): RevokeAccessResponse

    fun getAuthState(viewModelScope: CoroutineScope): AuthStateResponse
}
