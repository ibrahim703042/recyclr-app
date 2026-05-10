package com.gdsc.recyclr.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.gdsc.recyclr.domain.repository.AuthRepository
import com.gdsc.recyclr.domain.repository.SettingsRepository
import com.gdsc.recyclr.navigation.BottomBarPage
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
    private val settingsRepository: SettingsRepository
): ViewModel() {
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

    private val _pendingMainBottomTabRoute = MutableStateFlow<String?>(null)
    val pendingMainBottomTabRoute: StateFlow<String?> = _pendingMainBottomTabRoute.asStateFlow()

    fun requestOpenShopTab() {
        _pendingMainBottomTabRoute.value = BottomBarPage.Shop.route
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

}