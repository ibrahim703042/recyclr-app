package com.gdsc.recyclr.screens.auths.forgot_password.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.composable.EmailField
import com.gdsc.recyclr.components.design.AuthShell

@Composable
fun ForgotPasswordContent(
    sendPasswordResetEmail: (email: String) -> Unit,
) {
    var email by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    AuthShell(
        title = stringResource(R.string.auth_title_forgot_password),
        subtitle = stringResource(R.string.auth_subtitle_forgot_password),
    ) {
        EmailField(email = email, onEmailValueChange = { email = it })
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp)),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
            onClick = { sendPasswordResetEmail(email.text) },
        ) {
            Text(
                text = stringResource(R.string.auth_submit),
                fontSize = 20.sp,
                color = MaterialTheme.colors.onPrimary,
            )
        }
    }
}
