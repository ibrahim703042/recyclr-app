package com.gdsc.recyclr.data.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gdsc.recyclr.domain.model.AppLanguage
import com.gdsc.recyclr.domain.model.AppThemeMode
import com.gdsc.recyclr.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "recyclr_settings")

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : SettingsRepository {

    private val onboardingCompletedKey: Preferences.Key<Boolean> =
        booleanPreferencesKey("onboarding_completed")
    private val guestModeEnabledKey: Preferences.Key<Boolean> =
        booleanPreferencesKey("guest_mode_enabled")
    private val themeModeKey: Preferences.Key<String> =
        stringPreferencesKey("theme_mode")
    private val appLanguageKey: Preferences.Key<String> =
        stringPreferencesKey("app_language")

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

    override fun observeThemeMode(): Flow<AppThemeMode> {
        return context.dataStore.data.map { prefs ->
            AppThemeMode.fromStorage(prefs[themeModeKey])
        }
    }

    override suspend fun setThemeMode(mode: AppThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[themeModeKey] = mode.name
        }
    }

    override fun observeAppLanguage(): Flow<AppLanguage> {
        return context.dataStore.data.map { prefs ->
            AppLanguage.fromStorage(prefs[appLanguageKey])
        }
    }

    override suspend fun setAppLanguage(language: AppLanguage) {
        context.dataStore.edit { prefs ->
            prefs[appLanguageKey] = language.name
        }
    }
}
