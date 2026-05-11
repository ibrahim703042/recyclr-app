package com.gdsc.recyclr.screens.auths.sign_up.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
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
import com.gdsc.recyclr.components.composable.NameField
import com.gdsc.recyclr.components.composable.PasswordField
import com.gdsc.recyclr.components.design.AuthShell

@ExperimentalMaterialApi
@Composable
@ExperimentalComposeUiApi
fun SignUpContent(
    signUp: (name: String, email: String, password: String) -> Unit,
    navigateBack: () -> Unit,
) {
    var name by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var email by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var password by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    val keyboard = LocalSoftwareKeyboardController.current
    val nameText = name.text.trim()
    val emailText = email.text.trim()
    val passwordText = password.text
    val nameValid = nameText.length >= 2
    val emailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()
    val passwordValid = passwordText.length >= 6
    val canSubmit = nameValid && emailValid && passwordValid

    AuthShell(
        title = stringResource(R.string.auth_title_sign_up),
        subtitle = stringResource(R.string.auth_subtitle_sign_up),
    ) {
        NameField(
            name = name,
            onNameValueChange = { name = it },
            isError = nameText.isNotBlank() && !nameValid,
            errorText = stringResource(R.string.auth_invalid_name),
        )
        Spacer(modifier = Modifier.height(10.dp))

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
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp)),
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
            enabled = canSubmit,
            onClick = {
                keyboard?.hide()
                signUp(nameText, emailText, passwordText)
            },
        ) {
            Text(
                text = stringResource(R.string.auth_sign_up_action),
                fontSize = 20.sp,
                color = MaterialTheme.colors.onPrimary,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            ButtonAuth(
                icon = R.drawable.ic_google_icon,
                contentDescription = "Google Button",
                onClicked = {},
            )
            Spacer(modifier = Modifier.width(30.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(text = stringResource(R.string.auth_have_account))
            Spacer(modifier = Modifier.padding(3.dp))
            Text(
                modifier = Modifier.clickable { navigateBack() },
                text = stringResource(R.string.auth_sign_in),
                style = TextStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colors.primary),
            )
        }
    }
}
