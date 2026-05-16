package com.gdsc.recyclr.components.auth

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.composable.OtpInputField
import com.gdsc.recyclr.components.composable.RecyclrButton
import com.gdsc.recyclr.components.composable.RecyclrTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneVerificationSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    @StringRes titleRes: Int,
    phoneHint: String?,
    phoneVerificationId: String?,
    isVerifying: Boolean,
    onSendPhoneCode: (phoneE164: String) -> Unit,
    onVerifyPhoneCode: (code: String) -> Unit,
) {
    if (!visible) return

    var phone by rememberSaveable { mutableStateOf("") }
    var countryCode by rememberSaveable { mutableStateOf("+257") }
    var otpCode by rememberSaveable { mutableStateOf("") }

    val codeSent = !phoneVerificationId.isNullOrBlank()

    LaunchedEffect(phoneVerificationId) {
        if (phoneVerificationId.isNullOrBlank()) {
            otpCode = ""
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
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
                text = stringResource(titleRes),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(
                    if (codeSent) R.string.auth_otp_enter_code else R.string.auth_phone_sheet_body,
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (codeSent) {
                val masked = remember(phone, countryCode) {
                    val full = (countryCode + phone).filter { it.isDigit() || it == '+' }
                    if (full.length <= 4) full else "•••• ${full.takeLast(4)}"
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.auth_otp_sent_to, masked),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(Modifier.height(28.dp))

            if (!codeSent) {
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
                        singleLine = true,
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
                Spacer(Modifier.height(24.dp))
                RecyclrButton(
                    text = R.string.auth_send_sms,
                    onClick = { onSendPhoneCode(countryCode + phone) },
                    enabled = phone.isNotBlank() && !isVerifying,
                    isLoading = isVerifying,
                )
            } else {
                Text(
                    text = stringResource(R.string.auth_otp_label),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(16.dp))

                OtpInputField(
                    value = otpCode,
                    onValueChange = { otpCode = it },
                    enabled = !isVerifying,
                    onComplete = { code ->
                        if (!isVerifying) onVerifyPhoneCode(code)
                    },
                )

                Spacer(Modifier.height(24.dp))

                RecyclrButton(
                    text = R.string.auth_verify_code,
                    onClick = { onVerifyPhoneCode(otpCode) },
                    enabled = otpCode.length == 6 && !isVerifying,
                    isLoading = isVerifying,
                )

                Spacer(Modifier.height(12.dp))

                TextButton(
                    onClick = {
                        otpCode = ""
                        onSendPhoneCode(countryCode + phone)
                    },
                    enabled = !isVerifying,
                ) {
                    Text(stringResource(R.string.auth_otp_resend))
                }
            }

            phoneHint?.let { hint ->
                Spacer(Modifier.height(12.dp))
                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
