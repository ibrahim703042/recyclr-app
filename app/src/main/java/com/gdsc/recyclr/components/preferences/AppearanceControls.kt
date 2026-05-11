package com.gdsc.recyclr.components.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.gdsc.recyclr.R
import com.gdsc.recyclr.domain.model.AppLanguage
import com.gdsc.recyclr.domain.model.AppThemeMode
import com.gdsc.recyclr.ui.preferences.LocalAppAppearance

@Composable
fun ThemeToggleIconButton(modifier: Modifier = Modifier) {
    val appearance = LocalAppAppearance.current
    IconButton(
        modifier = modifier,
        onClick = appearance.cycleThemeMode,
    ) {
        Icon(
            imageVector = if (appearance.isDarkTheme) {
                Icons.Outlined.LightMode
            } else {
                Icons.Outlined.DarkMode
            },
            contentDescription = stringResource(R.string.theme_toggle),
            tint = MaterialTheme.colors.onBackground,
        )
    }
}

@Composable
fun LanguageSelectorRow(modifier: Modifier = Modifier) {
    val appearance = LocalAppAppearance.current
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = stringResource(R.string.language_label))
        LanguageChip(
            label = stringResource(R.string.language_system),
            selected = appearance.language == AppLanguage.SYSTEM,
            onClick = { appearance.setLanguage(AppLanguage.SYSTEM) },
        )
        LanguageChip(
            label = stringResource(R.string.language_english),
            selected = appearance.language == AppLanguage.EN,
            onClick = { appearance.setLanguage(AppLanguage.EN) },
        )
        LanguageChip(
            label = stringResource(R.string.language_french),
            selected = appearance.language == AppLanguage.FR,
            onClick = { appearance.setLanguage(AppLanguage.FR) },
        )
    }
}

@Composable
fun ThemeModeSelectorRow(modifier: Modifier = Modifier) {
    val appearance = LocalAppAppearance.current
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = stringResource(R.string.theme_label))
        ThemeChip(
            label = stringResource(R.string.theme_system),
            selected = appearance.themeMode == AppThemeMode.SYSTEM,
            onClick = { appearance.setThemeMode(AppThemeMode.SYSTEM) },
        )
        ThemeChip(
            label = stringResource(R.string.theme_light),
            selected = appearance.themeMode == AppThemeMode.LIGHT,
            onClick = { appearance.setThemeMode(AppThemeMode.LIGHT) },
        )
        ThemeChip(
            label = stringResource(R.string.theme_dark),
            selected = appearance.themeMode == AppThemeMode.DARK,
            onClick = { appearance.setThemeMode(AppThemeMode.DARK) },
        )
    }
}

@Composable
private fun LanguageChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    TextButton(onClick = onClick) {
        Text(
            text = label,
            color = if (selected) {
                MaterialTheme.colors.primary
            } else {
                MaterialTheme.colors.onBackground.copy(alpha = 0.7f)
            },
        )
    }
}

@Composable
private fun ThemeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    TextButton(onClick = onClick) {
        Text(
            text = label,
            color = if (selected) {
                MaterialTheme.colors.primary
            } else {
                MaterialTheme.colors.onBackground.copy(alpha = 0.7f)
            },
        )
    }
}
