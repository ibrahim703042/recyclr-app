package com.gdsc.recyclr.data.repository

import android.graphics.Bitmap
import android.app.Activity
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import com.gdsc.recyclr.data.service.AuthService
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.toFailure
import com.gdsc.recyclr.domain.model.User
import com.gdsc.recyclr.domain.repository.AuthRepository
import com.gdsc.recyclr.domain.repository.AuthStateResponse
import com.gdsc.recyclr.domain.repository.ReloadUserResponse
import com.gdsc.recyclr.domain.repository.RevokeAccessResponse
import com.gdsc.recyclr.domain.repository.SendEmailVerificationResponse
import com.gdsc.recyclr.domain.repository.SendPasswordResetEmailResponse
import com.gdsc.recyclr.domain.repository.SignInResponse
import com.gdsc.recyclr.domain.repository.SignUpResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService
) : AuthRepository {

    override val currentUser: User?
        get() = authService.peekCurrentUser()?.toDomain()

    override suspend fun getCurrentUser(): User? = authService.getCurrentUser()?.toDomain()

    override suspend fun signUpWithEmailAndPassword(
        name: String,
        email: String,
        password: String
    ): SignUpResponse {
        return authService.signUpWithEmailAndPassword(name, email, password)
            .fold(
                onSuccess = { Response.Success(it) },
                onFailure = { it.toFailure() }
            )
    }

    override suspend fun sendEmailVerification(): SendEmailVerificationResponse {
        return authService.sendEmailVerification()
            .fold(
                onSuccess = { Response.Success(it) },
                onFailure = { it.toFailure() }
            )
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): SignInResponse {
        return authService.signInWithEmailAndPassword(email, password)
            .fold(
                onSuccess = { Response.Success(it) },
                onFailure = { it.toFailure() }
            )
    }

    override suspend fun signInWithGoogleIdToken(idToken: String): SignInResponse {
        return authService.signInWithGoogleIdToken(idToken)
            .fold(
                onSuccess = { Response.Success(it) },
                onFailure = { it.toFailure() }
            )
    }

    override fun startPhoneVerification(
        activity: Activity,
        phoneNumberE164: String,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        authService.startPhoneVerification(activity, phoneNumberE164, callbacks)
    }

    override suspend fun signInWithPhoneSmsCode(verificationId: String, smsCode: String): SignInResponse {
        return authService.signInWithPhoneSmsCode(verificationId, smsCode)
            .fold(
                onSuccess = { Response.Success(it) },
                onFailure = { it.toFailure() }
            )
    }

    override suspend fun signInWithPhoneCredential(credential: PhoneAuthCredential): SignInResponse {
        return authService.signInWithPhoneCredential(credential)
            .fold(
                onSuccess = { Response.Success(it) },
                onFailure = { it.toFailure() }
            )
    }

    override suspend fun reloadUser(): ReloadUserResponse {
        return authService.reloadUser()
            .fold(
                onSuccess = { Response.Success(it) },
                onFailure = { it.toFailure() }
            )
    }

    override suspend fun sendPasswordResetEmail(email: String): SendPasswordResetEmailResponse {
        return authService.sendPasswordResetEmail(email)
            .fold(
                onSuccess = { Response.Success(it) },
                onFailure = { it.toFailure() }
            )
    }

    override suspend fun updateProfilePhoto(photoUrl: String): Response<Boolean> {
        return authService.updateProfilePhoto(photoUrl)
            .fold(
                onSuccess = { Response.Success(it) },
                onFailure = { it.toFailure() }
            )
    }

    override suspend fun uploadProfilePhoto(userId: String, bitmap: Bitmap): Response<String> {
        return authService.uploadProfilePhoto(userId, bitmap)
            .fold(
                onSuccess = { Response.Success(it) },
                onFailure = { it.toFailure() }
            )
    }

    override fun signOut() {
        authService.signOut()
    }

    override suspend fun revokeAccess(): RevokeAccessResponse {
        return authService.revokeAccess()
            .fold(
                onSuccess = { Response.Success(it) },
                onFailure = { it.toFailure() }
            )
    }

    override fun getAuthState(viewModelScope: CoroutineScope): AuthStateResponse {
        return authService.observeAuthState()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(),
                authService.peekCurrentUser() == null
            )
    }
}
