package com.gdsc.recyclr.screens.auths.sign_in.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.composable.ButtonAuth
import com.gdsc.recyclr.components.composable.EmailField
import com.gdsc.recyclr.components.composable.PasswordField

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

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        val contentMax = minOf(maxWidth, 520.dp)
        Column(
            Modifier
                .widthIn(max = contentMax)
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            SignDetail()
            Spacer(modifier = Modifier.size(40.dp))

            EmailField(
                email = email,
                onEmailValueChange = { email = it },
                isError = emailText.isNotBlank() && !emailValid,
                errorText = "E-mail invalide"
            )
            Spacer(modifier = Modifier.height(10.dp))

            PasswordField(
                password = password,
                onPasswordValueChange = { password = it },
                isError = passwordText.isNotBlank() && !passwordValid,
                errorText = "Mot de passe (6 caractères min.)"
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.clickable { navigateToForgotPasswordScreen() },
                    text = "Mot de passe oublié ?",
                    style = TextStyle(
                        color = MaterialTheme.colors.primary,
                        fontSize = 15.sp
                    )
                )
            }

            Spacer(modifier = Modifier.size(16.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp)),
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary),
                enabled = canSubmitEmail,
                onClick = {
                    keyboard?.hide()
                    signIn(emailText, passwordText)
                },
            ) {
                Text(
                    text = "CONNEXION",
                    fontSize = 18.sp,
                    color = MaterialTheme.colors.surface,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp)),
                onClick = { continueAsGuest() }
            ) {
                Text(text = "CONTINUER EN INVITÉ")
            }

            Spacer(modifier = Modifier.height(28.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Ou connexion avec", fontWeight = FontWeight.W400)
            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.Center) {
                ButtonAuth(
                    icon = R.drawable.ic_google_icon,
                    contentDescription = "Google",
                    onClicked = {
                        keyboard?.hide()
                        onGoogleClick()
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Numéro de téléphone", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("+243… (indicatif pays inclus)") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    keyboard?.hide()
                    onSendPhoneCode(phone)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Recevoir le code SMS")
            }
            if (!phoneVerificationId.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = smsCode,
                    onValueChange = { smsCode = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Code SMS") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        keyboard?.hide()
                        onVerifyPhoneCode(smsCode)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Vérifier le code")
                }
            }
            phoneHint?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, style = MaterialTheme.typography.caption, color = MaterialTheme.colors.primary)
            }

            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Pas encore de compte ?")
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    modifier = Modifier.clickable { navigateToSignUpScreen() },
                    text = "S'inscrire",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.primary,
                    )
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
