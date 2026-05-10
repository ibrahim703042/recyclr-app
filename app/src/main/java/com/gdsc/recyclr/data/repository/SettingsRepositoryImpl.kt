package com.gdsc.recyclr.data.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.gdsc.recyclr.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "recyclr_settings")

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private val onboardingCompletedKey: Preferences.Key<Boolean> =
        booleanPreferencesKey("onboarding_completed")
    private val guestModeEnabledKey: Preferences.Key<Boolean> =
        booleanPreferencesKey("guest_mode_enabled")

    override fun observeOnboardingCompleted(): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[onboardingCompletedKey] ?: false
        }
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[onboardingCompletedKey] = completed
        }
    }

    override fun observeGuestModeEnabled(): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[guestModeEnabledKey] ?: false
        }
    }

    override suspend fun setGuestModeEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[guestModeEnabledKey] = enabled
        }
    }
}

