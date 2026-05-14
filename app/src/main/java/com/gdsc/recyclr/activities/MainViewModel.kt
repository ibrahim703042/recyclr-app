package com.gdsc.recyclr.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdsc.recyclr.domain.model.AppLanguage
import com.gdsc.recyclr.domain.model.AppThemeMode
import com.gdsc.recyclr.domain.repository.AuthRepository
import com.gdsc.recyclr.domain.repository.SettingsRepository
import com.gdsc.recyclr.navigation.BottomBarPage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: AuthRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    init {
        getAuthState()
    }

    fun getAuthState() = repo.getAuthState(viewModelScope)

    val onboardingCompleted: StateFlow<Boolean> =
        settingsRepository.observeOnboardingCompleted()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val guestModeEnabled: StateFlow<Boolean> =
        settingsRepository.observeGuestModeEnabled()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val themeMode: StateFlow<AppThemeMode> =
        settingsRepository.observeThemeMode()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppThemeMode.SYSTEM)

    val appLanguage: StateFlow<AppLanguage> =
        settingsRepository.observeAppLanguage()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppLanguage.SYSTEM)

    private val _pendingMainBottomTabRoute = MutableStateFlow<String?>(null)
    val pendingMainBottomTabRoute: StateFlow<String?> = _pendingMainBottomTabRoute.asStateFlow()

    fun requestOpenShopTab() {
        _pendingMainBottomTabRoute.value = BottomBarPage.Shop.route
    }

    fun requestOpenMapTab() {
        _pendingMainBottomTabRoute.value = BottomBarPage.Map.route
    }

    fun consumePendingMainBottomTab() {
        _pendingMainBottomTabRoute.value = null
    }

    fun setOnboardingCompleted() {
        viewModelScope.launch {
            settingsRepository.setOnboardingCompleted(true)
        }
    }

    fun setGuestModeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setGuestModeEnabled(enabled)
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(mode)
        }
    }

    fun cycleThemeMode() {
        val next = when (themeMode.value) {
            AppThemeMode.SYSTEM -> AppThemeMode.LIGHT
            AppThemeMode.LIGHT -> AppThemeMode.DARK
            AppThemeMode.DARK -> AppThemeMode.SYSTEM
        }
        setThemeMode(next)
    }

    fun setAppLanguage(language: AppLanguage) {
        viewModelScope.launch {
            settingsRepository.setAppLanguage(language)
        }
    }

    fun signOut() = repo.signOut()

    fun revokeAccess() = viewModelScope.launch {
        repo.revokeAccess()
    }
}
