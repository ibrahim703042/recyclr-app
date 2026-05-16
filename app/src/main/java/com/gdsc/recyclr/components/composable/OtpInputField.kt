package com.gdsc.recyclr.components.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gdsc.recyclr.ui.theme.GreenHero

/**
 * Saisie OTP à 6 cases — collage, auto-avance, style Green Hero.
 */
@Composable
fun OtpInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    otpLength: Int = 6,
    enabled: Boolean = true,
    requestFocusOnLaunch: Boolean = true,
    onComplete: ((String) -> Unit)? = null,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(requestFocusOnLaunch, enabled) {
        if (requestFocusOnLaunch && enabled) {
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(value, otpLength) {
        if (value.length == otpLength) {
            onComplete?.invoke(value)
        }
    }

    BasicTextField(
        value = value,
        onValueChange = { raw ->
            val digits = raw.filter { it.isDigit() }.take(otpLength)
            if (digits != value) onValueChange(digits)
        },
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        enabled = enabled,
        singleLine = true,
        textStyle = TextStyle(
            color = Color.Transparent,
            fontSize = 1.sp,
        ),
        cursorBrush = SolidColor(Color.Transparent),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(otpLength) { index ->
                    val char = value.getOrNull(index)?.toString().orEmpty()
                    val isFocused = enabled && value.length == index
                    val isFilled = index < value.length
                    OtpCell(
                        char = char,
                        isFocused = isFocused,
                        isFilled = isFilled,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        },
    )
}

@Composable
private fun OtpCell(
    char: String,
    isFocused: Boolean,
    isFilled: Boolean,
    modifier: Modifier = Modifier,
) {
    val borderColor = when {
        isFocused -> GreenHero.Primary
        isFilled  -> GreenHero.Primary.copy(alpha = 0.5f)
        else      -> GreenHero.ChipBorder
    }
    val backgroundColor = when {
        isFocused || isFilled -> GreenHero.LightGreen
        else                  -> GreenHero.Card
    }

    Box(
        modifier = modifier
            .aspectRatio(0.85f)
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .background(backgroundColor, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = char,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = GreenHero.TextPrimary,
            textAlign = TextAlign.Center,
        )
    }
}
