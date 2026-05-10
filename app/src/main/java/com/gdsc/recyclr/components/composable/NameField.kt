package com.gdsc.recyclr.components.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun NameField(
    name: TextFieldValue,
    onNameValueChange: (newValue: TextFieldValue) -> Unit,
    isError: Boolean = false,
    errorText: String? = null,
) {
    val focusManager = LocalFocusManager.current

    Column {
        OutlinedTextField(
            value = name,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { newValue -> onNameValueChange(newValue) },
            label = { Text( text = "Name" ) },
            placeholder = { Text("Enter full name") },
            leadingIcon = { Icon( imageVector = Icons.Default.Person, contentDescription = "Name" ) },
            keyboardOptions = KeyboardOptions ( keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions( onNext = {focusManager.moveFocus(FocusDirection.Down) }),
            singleLine = true,
            isError = isError,
        )
        if (isError && !errorText.isNullOrBlank()) {
            Text(text = errorText, modifier = Modifier.fillMaxWidth())
        }
    }

}