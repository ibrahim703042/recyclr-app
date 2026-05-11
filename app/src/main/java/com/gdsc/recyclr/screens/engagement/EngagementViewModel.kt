package com.gdsc.recyclr.screens.engagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.engagement.CommunityPost
import com.gdsc.recyclr.domain.model.engagement.DonationCause
import com.gdsc.recyclr.domain.model.engagement.LeaderboardEntry
import com.gdsc.recyclr.domain.model.engagement.PickupRequestDraft
import com.gdsc.recyclr.domain.model.engagement.RecWallet
import com.gdsc.recyclr.domain.model.engagement.WeeklyChallenge
import com.gdsc.recyclr.domain.repository.AuthRepository
import com.gdsc.recyclr.domain.repository.EngagementRepository
import com.gdsc.recyclr.domain.repository.ImpactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EngagementViewModel @Inject constructor(
    private val engagementRepository: EngagementRepository,
    private val authRepository: AuthRepository,
    private val impactRepository: ImpactRepository,
) : ViewModel() {

    var challengeResponse: Response<WeeklyChallenge> = Response.Loading
        private set
    var leaderboardResponse: Response<List<LeaderboardEntry>> = Response.Loading
        private set
    var communityResponse: Response<List<CommunityPost>> = Response.Loading
        private set
    var walletResponse: Response<RecWallet> = Response.Loading
        private set
    var donationsResponse: Response<List<DonationCause>> = Response.Loading
        private set

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val uid = authRepository.currentUser?.uid ?: "guest"
            challengeResponse = engagementRepository.getChallenge(uid)
            leaderboardResponse = engagementRepository.getLeaderboard(uid)
            communityResponse = engagementRepository.getCommunityFeed(uid)
            donationsResponse = engagementRepository.getDonationCauses(uid)
            val impact = impactRepository.getUserImpact(uid)
            val points = (impact as? Response.Success)?.data?.pointsBalance ?: 0
            walletResponse = engagementRepository.getWallet(uid, points)
        }
    }

    suspend fun submitPickup(draft: PickupRequestDraft): Boolean {
        val uid = authRepository.currentUser?.uid ?: "guest"
        return when (val result = engagementRepository.submitPickupRequest(uid, draft)) {
            is Response.Success -> result.data == true
            else -> false
        }
    }
}
