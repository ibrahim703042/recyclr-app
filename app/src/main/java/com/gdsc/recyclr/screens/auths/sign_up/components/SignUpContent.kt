package com.gdsc.recyclr.screens.auths.sign_up.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.composable.*
import com.gdsc.recyclr.components.design.AuthShell

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpContent(
    signUp: (name: String, email: String, password: String) -> Unit,
    navigateBack: () -> Unit,
    phoneHint: String?,
    phoneVerificationId: String?,
    onSendPhoneCode: (phone: String) -> Unit,
    onVerifyPhoneCode: (code: String) -> Unit,
    onGoogleClick: () -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var countryCode by rememberSaveable { mutableStateOf("+1") }
    var smsCode by rememberSaveable { mutableStateOf("") }
    var showPhoneModal by rememberSaveable { mutableStateOf(false) }

    val keyboard = LocalSoftwareKeyboardController.current

    val nameValid = name.trim().length >= 2
    val emailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    val passwordValid = password.length >= 6
    val canSubmit = nameValid && emailValid && passwordValid

    val outlineColors = ButtonDefaults.outlinedButtonColors(
        contentColor = MaterialTheme.colorScheme.onSurface,
    )
    val outlineBorder = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)

    AuthShell(
        title = stringResource(R.string.auth_title_sign_up),
        subtitle = stringResource(R.string.auth_subtitle_sign_up),
    ) {
        NameField(
            name = name,
            onNameChange = { name = it },
            isError = name.isNotBlank() && !nameValid,
            errorText = stringResource(R.string.auth_invalid_name),
        )
        Spacer(modifier = Modifier.height(16.dp))

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
        Spacer(modifier = Modifier.height(32.dp))

        RecyclrButton(
            text = R.string.auth_sign_up_action,
            onClick = {
                keyboard?.hide()
                signUp(name.trim(), email.trim(), password)
            },
            enabled = canSubmit
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.auth_or_register_with),
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onGoogleClick,
                modifier = Modifier.weight(1f).height(56.dp),
                shape = MaterialTheme.shapes.medium,
                colors = outlineColors,
                border = outlineBorder,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google_icon),
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
                text = stringResource(R.string.auth_have_account),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                modifier = Modifier.clickable { navigateBack() },
                text = stringResource(R.string.auth_sign_in),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }

    if (showPhoneModal) {
        ModalBottomSheet(
            onDismissRequest = { showPhoneModal = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.auth_phone_sheet_title_sign_up),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.auth_phone_sheet_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = countryCode,
                        onValueChange = { countryCode = it },
                        modifier = Modifier.width(80.dp),
                        label = { Text(stringResource(R.string.auth_country_code_label)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        ),
                    )

                    RecyclrTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = stringResource(R.string.auth_phone_number),
                        placeholder = stringResource(R.string.auth_phone_hint),
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                RecyclrButton(
                    text = R.string.auth_send_sms,
                    onClick = { onSendPhoneCode(countryCode + phone) },
                    enabled = phone.isNotBlank(),
                )

                if (!phoneVerificationId.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(24.dp))

                    RecyclrTextField(
                        value = smsCode,
                        onValueChange = { smsCode = it },
                        label = stringResource(R.string.auth_sms_code),
                        placeholder = "123456",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    RecyclrButton(
                        text = R.string.auth_verify_code,
                        onClick = { onVerifyPhoneCode(smsCode) },
                        enabled = smsCode.length >= 6,
                    )
                }

                phoneHint?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
