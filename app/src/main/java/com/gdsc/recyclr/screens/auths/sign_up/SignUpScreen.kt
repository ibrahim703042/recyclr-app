package com.gdsc.recyclr.screens.auths.sign_up

import androidx.activity.ComponentActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.rememberCoroutineScope
import com.gdsc.recyclr.auth.GoogleCredentialAuth
import com.gdsc.recyclr.auth.GoogleSignInCancelled
import com.gdsc.recyclr.components.utils.UiUtils.showMessage
import com.gdsc.recyclr.screens.auths.sign_up.components.SignUp
import com.gdsc.recyclr.screens.auths.sign_up.components.SignUpContent
import com.gdsc.recyclr.util.AppLogger
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
@ExperimentalComposeUiApi
fun SignUpScreen(
    viewModel: SignUpViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context as ComponentActivity
    val scope = rememberCoroutineScope()

    SignUpContent(
        signUp = { name, email, password ->
            viewModel.signUpWithEmailAndPassword(name, email, password)
        },
        navigateBack = navigateBack,
        phoneHint = viewModel.phoneHint,
        phoneVerificationId = viewModel.phoneVerificationId,
        onSendPhoneCode = { phone -> viewModel.startPhoneVerification(activity, phone) },
        onVerifyPhoneCode = { code -> viewModel.verifyPhoneSmsCode(code) },
        onGoogleClick = {
            scope.launch {
                val webId = context.getString(com.gdsc.recyclr.R.string.default_web_client_id)
                GoogleCredentialAuth.getGoogleIdToken(activity, webId)
                    .onSuccess { token -> viewModel.signInWithGoogleIdToken(token) }
                    .onFailure { err ->
                        if (err !is GoogleSignInCancelled) {
                            AppLogger.w("Google sign-up", err)
                            showMessage(context, err.localizedMessage ?: "Google sign-up unavailable")
                        }
                    }
            }
        }
    )

    SignUp(
        viewModel = viewModel,
        showErrorMessage = { msg -> showMessage(context, msg.orEmpty()) },
    )
}
