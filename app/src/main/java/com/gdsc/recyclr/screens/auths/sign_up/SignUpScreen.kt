package com.gdsc.recyclr.screens.auths.sign_up

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
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
import com.gdsc.recyclr.screens.auths.sign_up.components.SignUp
import com.gdsc.recyclr.screens.auths.sign_up.components.SignUpContent
import com.gdsc.recyclr.util.AppLogger
import com.gdsc.recyclr.util.findComponentActivity
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
@ExperimentalComposeUiApi
fun SignUpScreen(
    viewModel: SignUpViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
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

    SignUpContent(
        signUp = { name, email, password ->
            viewModel.signUpWithEmailAndPassword(name, email, password)
        },
        navigateBack = navigateBack,
        phoneHint = viewModel.phoneHint,
        phoneVerificationId = viewModel.phoneVerificationId,
        isPhoneVerifying = viewModel.signUpResponse is Loading || viewModel.phoneSignInResponse is Loading,
        onSendPhoneCode = { phone -> viewModel.startPhoneVerification(activity, phone) },
        onVerifyPhoneCode = { code -> viewModel.verifyPhoneSmsCode(code) },
        onGoogleClick = {
            scope.launch {
                val webId = context.getString(R.string.default_web_client_id)
                GoogleCredentialAuth.getGoogleIdToken(activity, webId)
                    .onSuccess { token -> viewModel.signInWithGoogleIdToken(token) }
                    .onFailure { err ->
                        if (err !is GoogleSignInCancelled) {
                            AppLogger.w("Google sign-up", err)
                            showMessage(
                                context,
                                err.localizedMessage
                                    ?: context.getString(R.string.auth_google_unavailable),
                            )
                        }
                    }
            }
        },
    )

    SignUp(
        viewModel = viewModel,
        showErrorMessage = { msg ->
            showMessage(
                context,
                msg?.takeIf { it.isNotBlank() } ?: context.getString(R.string.auth_error_generic),
            )
        },
    )
}
