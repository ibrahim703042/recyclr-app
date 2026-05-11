package com.gdsc.recyclr.domain.repository

import com.gdsc.recyclr.domain.model.AppLanguage
import com.gdsc.recyclr.domain.model.AppThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeOnboardingCompleted(): Flow<Boolean>
    suspend fun setOnboardingCompleted(completed: Boolean)

    fun observeGuestModeEnabled(): Flow<Boolean>
    suspend fun setGuestModeEnabled(enabled: Boolean)

    fun observeThemeMode(): Flow<AppThemeMode>
    suspend fun setThemeMode(mode: AppThemeMode)

    fun observeAppLanguage(): Flow<AppLanguage>
    suspend fun setAppLanguage(language: AppLanguage)
}
