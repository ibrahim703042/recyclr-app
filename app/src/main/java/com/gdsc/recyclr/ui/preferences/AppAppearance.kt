package com.gdsc.recyclr.ui.preferences

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import com.gdsc.recyclr.domain.model.AppLanguage
import com.gdsc.recyclr.domain.model.AppThemeMode

@Stable
data class AppAppearanceState(
    val themeMode: AppThemeMode,
    val language: AppLanguage,
    val isDarkTheme: Boolean,
    val setThemeMode: (AppThemeMode) -> Unit,
    val setLanguage: (AppLanguage) -> Unit,
    val cycleThemeMode: () -> Unit,
)

val LocalAppAppearance = staticCompositionLocalOf<AppAppearanceState> {
    error("AppAppearanceState is not provided")
}

@Composable
fun resolveIsDarkTheme(themeMode: AppThemeMode): Boolean {
    val systemDark = isSystemInDarkTheme()
    return when (themeMode) {
        AppThemeMode.SYSTEM -> systemDark
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }
}
