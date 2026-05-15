package com.gdsc.recyclr.screens.auths.sign_in

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.gdsc.recyclr.R
import com.gdsc.recyclr.auth.GoogleCredentialAuth
import com.gdsc.recyclr.auth.GoogleSignInCancelled
import com.gdsc.recyclr.components.utils.UiUtils.showMessage
import com.gdsc.recyclr.screens.auths.sign_in.components.SignIn
import com.gdsc.recyclr.screens.auths.sign_in.components.SignInContent
import com.gdsc.recyclr.util.AppLogger
import kotlinx.coroutines.launch

@Composable
@ExperimentalComposeUiApi
fun SignInScreen(
    viewModel: SignInViewModel = hiltViewModel(),
    navigateToForgotPasswordScreen: () -> Unit,
    navigateToSignUpScreen: () -> Unit,
    continueAsGuest: () -> Unit,
) {
    val vm = viewModel
    val context = LocalContext.current
    val activity = context as ComponentActivity
    val scope = rememberCoroutineScope()

    SignInContent(
        signIn = { email, password -> vm.signInWithEmailAndPassword(email, password) },
        navigateToForgotPasswordScreen = navigateToForgotPasswordScreen,
        navigateToSignUpScreen = navigateToSignUpScreen,
        continueAsGuest = continueAsGuest,
        phoneHint = vm.phoneHint,
        phoneVerificationId = vm.phoneVerificationId,
        onGoogleClick = {
            scope.launch {
                val webId = context.getString(R.string.default_web_client_id)
                GoogleCredentialAuth.getGoogleIdToken(activity, webId)
                    .onSuccess { token -> vm.signInWithGoogleIdToken(token) }
                    .onFailure { err ->
                        when (err) {
                            is GoogleSignInCancelled -> Unit
                            else -> {
                                AppLogger.w("Google Credential Manager", err)
                                showMessage(
                                    context,
                                    err.localizedMessage
                                        ?: context.getString(R.string.auth_google_unavailable),
                                )
                            }
                        }
                    }
            }
        },
        onSendPhoneCode = { phone -> vm.startPhoneVerification(activity, phone) },
        onVerifyPhoneCode = { code -> vm.verifyPhoneSmsCode(code) }
    )

    SignIn(
        viewModel = vm,
        showErrorMessage = { msg -> showMessage(context, msg) }
    )
}
