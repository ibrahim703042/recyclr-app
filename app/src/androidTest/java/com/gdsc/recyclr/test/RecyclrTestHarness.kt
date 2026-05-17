package com.gdsc.recyclr.test

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.gdsc.recyclr.domain.model.AppLanguage
import com.gdsc.recyclr.domain.model.AppThemeMode
import com.gdsc.recyclr.ui.preferences.AppAppearanceState
import com.gdsc.recyclr.ui.preferences.LocalAppAppearance
import com.gdsc.recyclr.ui.theme.RecyclrTheme

fun testAppAppearance(isDark: Boolean): AppAppearanceState = AppAppearanceState(
    themeMode = if (isDark) AppThemeMode.DARK else AppThemeMode.LIGHT,
    language = AppLanguage.EN,
    isDarkTheme = isDark,
    setThemeMode = {},
    setLanguage = {},
    cycleThemeMode = {},
)

@Composable
fun RecyclrTestHarness(
    isDark: Boolean,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalAppAppearance provides testAppAppearance(isDark)) {
        RecyclrTheme(darkTheme = isDark) {
            content()
        }
    }
}
