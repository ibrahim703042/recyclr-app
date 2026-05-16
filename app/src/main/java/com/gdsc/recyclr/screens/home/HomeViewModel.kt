package com.gdsc.recyclr.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdsc.recyclr.data.local.dao.NotificationDao
import com.gdsc.recyclr.data.service.LiveActivity
import com.gdsc.recyclr.data.service.LiveTickerService
import com.gdsc.recyclr.domain.model.User
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.Response.Loading
import com.gdsc.recyclr.domain.model.UserImpact
import com.gdsc.recyclr.domain.model.engagement.HomeDashboard
import com.gdsc.recyclr.domain.repository.AuthRepository
import com.gdsc.recyclr.domain.repository.EngagementRepository
import com.gdsc.recyclr.domain.repository.ImpactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val impactRepository: ImpactRepository,
    private val engagementRepository: EngagementRepository,
    private val notificationDao: NotificationDao,
    private val liveTickerService: LiveTickerService,
) : ViewModel() {

    var latestLiveActivity by mutableStateOf<LiveActivity?>(null)
        private set

    var currentUserWithRole by mutableStateOf<User?>(null)
        private set

    val currentUser get() = currentUserWithRole ?: authRepository.currentUser

    /** Prénom ou pseudo pour l’accueil (README V4). */
    val welcomeName: String
        get() = currentUser?.displayName?.takeIf { !it.isNullOrBlank() }
            ?: currentUser?.email?.substringBefore('@')?.takeIf { it.isNotBlank() }
            ?: "Green Hero"

    var impactResponse: Response<UserImpact> by mutableStateOf(Loading)
        private set

    var dashboardResponse: Response<HomeDashboard> by mutableStateOf(Loading)
        private set

    val unreadNotificationCount: StateFlow<Int> = notificationDao.observeUnreadCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    init {
        observeImpact()
        refreshDashboard()
        fetchUserWithRole()
        observeLiveTicker()
    }

    private fun observeLiveTicker() {
        viewModelScope.launch {
            liveTickerService.observeLiveActivity().collect { activity ->
                latestLiveActivity = activity
            }
        }
    }

    private fun fetchUserWithRole() {
        viewModelScope.launch {
            currentUserWithRole = authRepository.getCurrentUser()
        }
    }

    private fun observeImpact() {
        val uid = authRepository.currentUser?.uid ?: "guest"
        viewModelScope.launch {
            impactRepository.observeUserImpact(uid).collectLatest {
                impactResponse = it
                // Refresh dashboard when points change
                if (it is Response.Success) {
                    refreshDashboard(it.data?.pointsBalance ?: 0)
                }
            }
        }
    }

    fun refreshDashboard(points: Int? = null) {
        val uid = authRepository.currentUser?.uid ?: "guest"
        val currentPoints = points ?: (impactResponse as? Response.Success)?.data?.pointsBalance ?: 0
        viewModelScope.launch {
            dashboardResponse = Loading
            dashboardResponse = engagementRepository.getHomeDashboard(uid, currentPoints)
        }
    }
}
