package com.gdsc.recyclr.ui.preferences

import com.gdsc.recyclr.domain.model.AppThemeMode
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ResolveIsDarkThemeTest {

    @Test
    fun lightMode_isNeverDark() {
        assertFalse(resolveIsDarkThemeForMode(AppThemeMode.LIGHT, systemDark = true))
        assertFalse(resolveIsDarkThemeForMode(AppThemeMode.LIGHT, systemDark = false))
    }

    @Test
    fun darkMode_isAlwaysDark() {
        assertTrue(resolveIsDarkThemeForMode(AppThemeMode.DARK, systemDark = true))
        assertTrue(resolveIsDarkThemeForMode(AppThemeMode.DARK, systemDark = false))
    }

    @Test
    fun systemMode_followsSystemSetting() {
        assertTrue(resolveIsDarkThemeForMode(AppThemeMode.SYSTEM, systemDark = true))
        assertFalse(resolveIsDarkThemeForMode(AppThemeMode.SYSTEM, systemDark = false))
    }
}
