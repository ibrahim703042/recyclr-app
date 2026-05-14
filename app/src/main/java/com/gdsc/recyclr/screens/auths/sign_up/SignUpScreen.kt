package com.gdsc.recyclr.screens.auths.sign_up

import androidx.activity.ComponentActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.gdsc.recyclr.components.utils.UiUtils.showMessage
import com.gdsc.recyclr.screens.auths.sign_up.components.SignUp
import com.gdsc.recyclr.screens.auths.sign_up.components.SignUpContent

@ExperimentalMaterialApi
@Composable
@ExperimentalComposeUiApi
fun SignUpScreen(
    viewModel: SignUpViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context as ComponentActivity

    SignUpContent(
        signUp = { name, email, password ->
            viewModel.signUpWithEmailAndPassword(name, email, password)
        },
        navigateBack = navigateBack,
        phoneHint = viewModel.phoneHint,
        phoneVerificationId = viewModel.phoneVerificationId,
        onSendPhoneCode = { phone -> viewModel.startPhoneVerification(activity, phone) },
        onVerifyPhoneCode = { code -> viewModel.verifyPhoneSmsCode(code) },
    )

    SignUp(
        viewModel = viewModel,
        showErrorMessage = { msg -> showMessage(context, msg.orEmpty()) },
    )
}
