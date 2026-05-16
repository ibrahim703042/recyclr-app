package com.gdsc.recyclr.screens.auths.sign_in

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.gdsc.recyclr.domain.model.Response.Loading
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gdsc.recyclr.R
import com.gdsc.recyclr.auth.GoogleCredentialAuth
import com.gdsc.recyclr.auth.GoogleSignInCancelled
import com.gdsc.recyclr.components.utils.UiUtils.showMessage
import com.gdsc.recyclr.screens.auths.sign_in.components.SignIn
import com.gdsc.recyclr.screens.auths.sign_in.components.SignInContent
import com.gdsc.recyclr.util.AppLogger
import com.gdsc.recyclr.util.findComponentActivity
import kotlinx.coroutines.launch

@Composable
@ExperimentalComposeUiApi
fun SignInScreen(
    viewModel: SignInViewModel = hiltViewModel(),
    navigateToForgotPasswordScreen: () -> Unit,
    navigateToSignUpScreen: () -> Unit,
    continueAsGuest: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context.findComponentActivity()
    val scope = rememberCoroutineScope()

    if (activity == null) {
        Box(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.auth_activity_unavailable),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
            )
        }
        return
    }

    SignInContent(
        signIn = { email, password -> viewModel.signInWithEmailAndPassword(email, password) },
        navigateToForgotPasswordScreen = navigateToForgotPasswordScreen,
        navigateToSignUpScreen = navigateToSignUpScreen,
        continueAsGuest = continueAsGuest,
        phoneHint = viewModel.phoneHint,
        phoneVerificationId = viewModel.phoneVerificationId,
        isPhoneVerifying = viewModel.signInResponse is Loading,
        onGoogleClick = {
            scope.launch {
                val webId = context.getString(R.string.default_web_client_id)
                GoogleCredentialAuth.getGoogleIdToken(activity, webId)
                    .onSuccess { token -> viewModel.signInWithGoogleIdToken(token) }
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
        onSendPhoneCode = { phone -> viewModel.startPhoneVerification(activity, phone) },
        onVerifyPhoneCode = { code -> viewModel.verifyPhoneSmsCode(code) },
    )

    SignIn(
        viewModel = viewModel,
        showErrorMessage = { msg ->
            showMessage(
                context,
                msg?.takeIf { it.isNotBlank() } ?: context.getString(R.string.auth_error_generic),
            )
        },
    )
}
