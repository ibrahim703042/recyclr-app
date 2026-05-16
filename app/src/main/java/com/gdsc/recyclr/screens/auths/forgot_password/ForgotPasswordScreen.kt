package com.gdsc.recyclr.screens.auths.forgot_password

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.utils.UiUtils.showMessage
import com.gdsc.recyclr.screens.auths.forgot_password.components.ForgotPassword
import com.gdsc.recyclr.screens.auths.forgot_password.components.ForgotPasswordContent

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current

    ForgotPasswordContent(
        sendPasswordResetEmail = { email ->
            viewModel.sendPasswordResetEmail(email)
        },
    )

    ForgotPassword(
        viewModel = viewModel,
        navigateBack = navigateBack,
        showResetPasswordMessage = {
            showMessage(context, context.getString(R.string.auth_reset_email_sent))
        },
        showErrorMessage = { errorMessage ->
            showMessage(
                context,
                errorMessage?.takeIf { it.isNotBlank() }
                    ?: context.getString(R.string.auth_error_generic),
            )
        },
    )
}
