package com.gdsc.recyclr.screens.auths.sign_in.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.composable.ButtonAuth
import com.gdsc.recyclr.components.composable.EmailField
import com.gdsc.recyclr.components.composable.PasswordField
import com.gdsc.recyclr.components.design.AuthShell

@OptIn(ExperimentalMaterialApi::class)
@Composable
@ExperimentalComposeUiApi
fun SignInContent(
    signIn: (email: String, password: String) -> Unit,
    navigateToForgotPasswordScreen: () -> Unit,
    navigateToSignUpScreen: () -> Unit,
    continueAsGuest: () -> Unit,
    phoneHint: String?,
    phoneVerificationId: String?,
    onGoogleClick: () -> Unit,
    onSendPhoneCode: (phone: String) -> Unit,
    onVerifyPhoneCode: (code: String) -> Unit,
) {
    var email by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var password by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var phone by rememberSaveable { mutableStateOf("") }
    var smsCode by rememberSaveable { mutableStateOf("") }

    val keyboard = LocalSoftwareKeyboardController.current
    val emailText = email.text.trim()
    val passwordText = password.text
    val emailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()
    val passwordValid = passwordText.length >= 6
    val canSubmitEmail = emailValid && passwordValid

    AuthShell(
        title = stringResource(R.string.auth_title_sign_in),
        subtitle = stringResource(R.string.auth_subtitle_sign_in),
    ) {
        EmailField(
            email = email,
            onEmailValueChange = { email = it },
            isError = emailText.isNotBlank() && !emailValid,
            errorText = stringResource(R.string.auth_invalid_email),
        )
        Spacer(modifier = Modifier.height(10.dp))

        PasswordField(
            password = password,
            onPasswordValueChange = { password = it },
            isError = passwordText.isNotBlank() && !passwordValid,
            errorText = stringResource(R.string.auth_invalid_password),
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            modifier = Modifier.clickable { navigateToForgotPasswordScreen() },
            text = stringResource(R.string.auth_forgot_password),
            style = TextStyle(color = MaterialTheme.colors.primary, fontSize = 15.sp),
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp)),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
            enabled = canSubmitEmail,
            onClick = {
                keyboard?.hide()
                signIn(emailText, passwordText)
            },
        ) {
            Text(
                text = stringResource(R.string.auth_sign_in),
                fontSize = 18.sp,
                color = MaterialTheme.colors.onPrimary,
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp)),
            onClick = { continueAsGuest() },
        ) {
            Text(text = stringResource(R.string.auth_continue_guest))
        }

        Spacer(modifier = Modifier.height(24.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = stringResource(R.string.auth_or_sign_in_with), fontWeight = FontWeight.Normal)
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            ButtonAuth(
                icon = R.drawable.ic_google_icon,
                contentDescription = "Google",
                onClicked = {
                    keyboard?.hide()
                    onGoogleClick()
                },
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = stringResource(R.string.auth_phone_number), fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.auth_phone_hint)) },
            singleLine = true,
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = {
                keyboard?.hide()
                onSendPhoneCode(phone)
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.auth_send_sms))
        }
        if (!phoneVerificationId.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = smsCode,
                onValueChange = { smsCode = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.auth_sms_code)) },
                singleLine = true,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    keyboard?.hide()
                    onVerifyPhoneCode(smsCode)
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.auth_verify_code))
            }
        }
        phoneHint?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, style = MaterialTheme.typography.caption, color = MaterialTheme.colors.primary)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(text = stringResource(R.string.auth_no_account))
            Spacer(modifier = Modifier.padding(4.dp))
            Text(
                modifier = Modifier.clickable { navigateToSignUpScreen() },
                text = stringResource(R.string.auth_sign_up),
                style = TextStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colors.primary),
            )
        }
    }
}
