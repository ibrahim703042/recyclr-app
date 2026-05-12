package com.gdsc.recyclr.components.preferences

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
            tint = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
fun LanguageSelectorRow(modifier: Modifier = Modifier) {
    val appearance = LocalAppAppearance.current
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.language_label),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            LanguageChip(
                label = stringResource(R.string.language_system),
                selected = appearance.language == AppLanguage.SYSTEM,
                onClick = { appearance.setLanguage(AppLanguage.SYSTEM) },
                modifier = Modifier.weight(1f)
            )
            LanguageChip(
                label = "EN",
                selected = appearance.language == AppLanguage.EN,
                onClick = { appearance.setLanguage(AppLanguage.EN) },
                modifier = Modifier.weight(1f)
            )
            LanguageChip(
                label = "FR",
                selected = appearance.language == AppLanguage.FR,
                onClick = { appearance.setLanguage(AppLanguage.FR) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ThemeModeSelectorRow(modifier: Modifier = Modifier) {
    val appearance = LocalAppAppearance.current
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.theme_label),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            ThemeChip(
                label = "System",
                selected = appearance.themeMode == AppThemeMode.SYSTEM,
                onClick = { appearance.setThemeMode(AppThemeMode.SYSTEM) },
                modifier = Modifier.weight(1f)
            )
            ThemeChip(
                label = "Light",
                selected = appearance.themeMode == AppThemeMode.LIGHT,
                onClick = { appearance.setThemeMode(AppThemeMode.LIGHT) },
                modifier = Modifier.weight(1f)
            )
            ThemeChip(
                label = "Dark",
                selected = appearance.themeMode == AppThemeMode.DARK,
                onClick = { appearance.setThemeMode(AppThemeMode.DARK) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.labelLarge) },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.labelLarge) },
        modifier = modifier
    )
}
