package com.gdsc.recyclr.screens.auths.sign_in

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdsc.recyclr.domain.model.Response.Loading
import com.gdsc.recyclr.domain.model.Response.Success
import com.gdsc.recyclr.domain.repository.AuthRepository
import com.gdsc.recyclr.domain.repository.SignInResponse
import com.gdsc.recyclr.util.AppLogger
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    var signInResponse by mutableStateOf<SignInResponse>(Success(false))
        private set

    var phoneVerificationId by mutableStateOf<String?>(null)
        private set

    var phoneHint by mutableStateOf<String?>(null)
        private set

    fun resetSignInResponse() {
        signInResponse = Success(false)
    }

    fun signInWithEmailAndPassword(email: String, password: String) = viewModelScope.launch {
        signInResponse = Loading
        signInResponse = repo.signInWithEmailAndPassword(email, password)
    }

    fun signInWithGoogleIdToken(idToken: String?) = viewModelScope.launch {
        if (idToken.isNullOrBlank()) {
            AppLogger.w("Google Sign-In : idToken vide")
            signInResponse = com.gdsc.recyclr.domain.model.Response.Failure(
                IllegalStateException("Connexion Google indisponible (jeton manquant). Vérifiez default_web_client_id.")
            )
            return@launch
        }
        signInResponse = Loading
        signInResponse = repo.signInWithGoogleIdToken(idToken)
    }

    fun buildPhoneCallbacks(): PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                viewModelScope.launch {
                    signInResponse = Loading
                    signInResponse = repo.signInWithPhoneCredential(credential)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                AppLogger.w("Phone verification failed", e)
                phoneHint = e.localizedMessage
                signInResponse = com.gdsc.recyclr.domain.model.Response.Failure(e)
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                phoneVerificationId = verificationId
                phoneHint = "Code envoyé. Saisissez le SMS."
            }
        }

    fun startPhoneVerification(activity: android.app.Activity, phoneRaw: String) {
        val trimmed = phoneRaw.trim()
        if (trimmed.isBlank()) {
            phoneHint = "Numéro invalide"
            return
        }
        val e164 = if (trimmed.startsWith("+")) trimmed else "+$trimmed"
        phoneHint = null
        runCatching {
            repo.startPhoneVerification(activity, e164, buildPhoneCallbacks())
        }.onFailure {
            AppLogger.e("startPhoneVerification", it)
            phoneHint = it.message
        }
    }

    fun verifyPhoneSmsCode(code: String) = viewModelScope.launch {
        val vid = phoneVerificationId
        if (vid.isNullOrBlank()) {
            phoneHint = "Demandez d'abord un code SMS."
            return@launch
        }
        signInResponse = Loading
        signInResponse = repo.signInWithPhoneSmsCode(vid, code.trim())
    }
}
