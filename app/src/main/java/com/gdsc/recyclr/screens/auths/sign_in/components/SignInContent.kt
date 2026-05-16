package com.gdsc.recyclr.screens.auths.sign_in.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.auth.PhoneVerificationSheet
import com.gdsc.recyclr.components.composable.*
import com.gdsc.recyclr.components.design.AuthShell

@Composable
fun SignInContent(
    signIn: (email: String, password: String) -> Unit,
    navigateToForgotPasswordScreen: () -> Unit,
    navigateToSignUpScreen: () -> Unit,
    continueAsGuest: () -> Unit,
    phoneHint: String?,
    phoneVerificationId: String?,
    isPhoneVerifying: Boolean,
    onGoogleClick: () -> Unit,
    onSendPhoneCode: (phone: String) -> Unit,
    onVerifyPhoneCode: (code: String) -> Unit,
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPhoneModal by remember { mutableStateOf(false) }

    val keyboard = LocalSoftwareKeyboardController.current
    val outlineColors = ButtonDefaults.outlinedButtonColors(
        contentColor = MaterialTheme.colorScheme.onSurface,
    )
    val outlineBorder = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)

    val emailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    val passwordValid = password.length >= 6
    val canSubmitEmail = emailValid && passwordValid

    AuthShell(
        title = stringResource(R.string.auth_title_sign_in),
        subtitle = stringResource(R.string.auth_subtitle_sign_in),
    ) {
        EmailField(
            email = email,
            onEmailChange = { email = it },
            isError = email.isNotBlank() && !emailValid,
            errorText = stringResource(R.string.auth_invalid_email),
        )
        Spacer(modifier = Modifier.height(16.dp))

        PasswordField(
            password = password,
            onPasswordChange = { password = it },
            isError = password.isNotBlank() && !passwordValid,
            errorText = stringResource(R.string.auth_invalid_password),
        )
        
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            Text(
                modifier = Modifier
                    .clickable { navigateToForgotPasswordScreen() }
                    .padding(vertical = 8.dp),
                text = stringResource(R.string.auth_forgot_password),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        RecyclrButton(
            text = R.string.auth_sign_in,
            onClick = {
                keyboard?.hide()
                signIn(email.trim(), password)
            },
            enabled = canSubmitEmail
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedButton(
            modifier = Modifier.fillMaxWidth().height(56.dp),
            onClick = { continueAsGuest() },
            shape = MaterialTheme.shapes.medium,
            colors = outlineColors,
            border = outlineBorder,
        ) {
            Text(text = stringResource(R.string.auth_continue_guest))
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.auth_or_sign_in_with),
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = {
                    keyboard?.hide()
                    onGoogleClick()
                },
                modifier = Modifier.weight(1f).height(56.dp),
                shape = MaterialTheme.shapes.medium,
                colors = outlineColors,
                border = outlineBorder,
            ) {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_google_icon),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.auth_provider_google))
            }

            OutlinedButton(
                onClick = { showPhoneModal = true },
                modifier = Modifier.weight(1f).height(56.dp),
                shape = MaterialTheme.shapes.medium,
                colors = outlineColors,
                border = outlineBorder,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Phone,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.auth_provider_phone))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        Row(
            modifier = Modifier.padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(R.string.auth_no_account),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                modifier = Modifier.clickable { navigateToSignUpScreen() },
                text = stringResource(R.string.auth_sign_up),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    PhoneVerificationSheet(
        visible = showPhoneModal,
        onDismiss = { showPhoneModal = false },
        titleRes = R.string.auth_phone_sheet_title_sign_in,
        phoneHint = phoneHint,
        phoneVerificationId = phoneVerificationId,
        isVerifying = isPhoneVerifying,
        onSendPhoneCode = onSendPhoneCode,
        onVerifyPhoneCode = onVerifyPhoneCode,
    )
}
