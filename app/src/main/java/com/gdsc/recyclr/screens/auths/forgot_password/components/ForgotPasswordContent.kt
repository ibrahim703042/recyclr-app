package com.gdsc.recyclr.screens.auths.forgot_password.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.composable.EmailField
import com.gdsc.recyclr.components.composable.RecyclrButton
import com.gdsc.recyclr.components.design.AuthShell

@Composable
fun ForgotPasswordContent(
    sendPasswordResetEmail: (email: String) -> Unit,
) {
    var email by rememberSaveable { mutableStateOf("") }

    AuthShell(
        title = stringResource(R.string.auth_title_forgot_password),
        subtitle = stringResource(R.string.auth_subtitle_forgot_password),
    ) {
        EmailField(
            email = email,
            onEmailChange = { email = it }
        )
        Spacer(modifier = Modifier.height(32.dp))
        RecyclrButton(
            text = R.string.auth_submit,
            onClick = { sendPasswordResetEmail(email) },
            enabled = android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
        )
    }
}
