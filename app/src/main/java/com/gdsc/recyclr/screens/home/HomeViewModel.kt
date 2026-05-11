package com.gdsc.recyclr.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.Response.Loading
import com.gdsc.recyclr.domain.model.UserImpact
import com.gdsc.recyclr.domain.model.engagement.HomeDashboard
import com.gdsc.recyclr.domain.repository.AuthRepository
import com.gdsc.recyclr.domain.repository.EngagementRepository
import com.gdsc.recyclr.domain.repository.ImpactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val impactRepository: ImpactRepository,
    private val engagementRepository: EngagementRepository,
) : ViewModel() {

    val currentUser get() = authRepository.currentUser

    var impactResponse: Response<UserImpact> = Loading
        private set

    var dashboardResponse: Response<HomeDashboard> = Loading
        private set

    init {
        refresh()
    }

    fun refresh() {
        val uid = authRepository.currentUser?.uid ?: "guest"
        viewModelScope.launch {
            impactResponse = Loading
            dashboardResponse = Loading
            impactResponse = impactRepository.getUserImpact(uid)
            val points = (impactResponse as? Response.Success)?.data?.pointsBalance ?: 0
            dashboardResponse = engagementRepository.getHomeDashboard(uid, points)
        }
    }
}
