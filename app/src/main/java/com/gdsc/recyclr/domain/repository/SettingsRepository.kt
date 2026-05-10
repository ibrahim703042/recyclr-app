package com.gdsc.recyclr.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeOnboardingCompleted(): Flow<Boolean>
    suspend fun setOnboardingCompleted(completed: Boolean)

    fun observeGuestModeEnabled(): Flow<Boolean>
    suspend fun setGuestModeEnabled(enabled: Boolean)
}

