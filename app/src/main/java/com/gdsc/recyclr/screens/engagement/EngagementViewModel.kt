package com.gdsc.recyclr.screens.engagement

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdsc.recyclr.domain.model.UserRole
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.engagement.CommunityPost
import com.gdsc.recyclr.domain.model.engagement.DonationCause
import com.gdsc.recyclr.domain.model.engagement.LeaderboardEntry
import com.gdsc.recyclr.domain.model.engagement.PickupRequestDraft
import com.gdsc.recyclr.domain.model.engagement.RecWallet
import com.gdsc.recyclr.domain.model.engagement.WalletTransaction
import com.gdsc.recyclr.domain.model.engagement.WeeklyChallenge
import com.gdsc.recyclr.domain.repository.AuthRepository
import com.gdsc.recyclr.domain.repository.EngagementRepository
import com.gdsc.recyclr.domain.repository.ImpactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EngagementViewModel @Inject constructor(
    private val engagementRepository: EngagementRepository,
    private val authRepository: AuthRepository,
    private val impactRepository: ImpactRepository,
) : ViewModel() {

    var userRole by mutableStateOf(UserRole.USER)
        private set

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
    var currentUserLeaderboardEntry by mutableStateOf<LeaderboardEntry?>(null)
        private set
    
    // Transaction history
    var transactionHistoryResponse: Response<List<WalletTransaction>> = Response.Loading
        private set
    
    // Operation states
    var sellOperationState by mutableStateOf<OperationState>(OperationState.Idle)
        private set
    
    var withdrawOperationState by mutableStateOf<OperationState>(OperationState.Idle)
        private set
    
    var sendOperationState by mutableStateOf<OperationState>(OperationState.Idle)
        private set

    init {
        refresh()
        fetchRole()
        loadTransactionHistory()
    }

    private fun fetchRole() {
        viewModelScope.launch {
            userRole = authRepository.getCurrentUser()?.role ?: UserRole.USER
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val uid = authRepository.currentUser?.uid ?: "guest"
            challengeResponse = engagementRepository.getChallenge(uid)
            val lb = engagementRepository.getLeaderboard(uid)
            leaderboardResponse = lb
            if (lb is Response.Success) {
                currentUserLeaderboardEntry = lb.data?.find { it.isCurrentUser }
            }

            communityResponse = engagementRepository.getCommunityFeed(uid)
            donationsResponse = engagementRepository.getDonationCauses(uid)
            val impact = impactRepository.getUserImpact(uid)
            val points = (impact as? Response.Success)?.data?.pointsBalance ?: 0
            walletResponse = engagementRepository.getWallet(uid, points)
        }
    }
    
    fun loadTransactionHistory() {
        viewModelScope.launch {
            val uid = authRepository.currentUser?.uid ?: return@launch
            transactionHistoryResponse = Response.Loading
            transactionHistoryResponse = engagementRepository.getTransactionHistory(uid)
        }
    }
    
    fun sellCarbonCredits(amount: Float) {
        viewModelScope.launch {
            try {
                sellOperationState = OperationState.Loading
                val uid = authRepository.currentUser?.uid ?: return@launch
                
                when (val result = engagementRepository.sellCarbonCredits(uid, amount)) {
                    is Response.Success -> {
                        sellOperationState = OperationState.Success(result.data?.message ?: "Sale successful")
                        refresh()
                        loadTransactionHistory()
                    }
                    is Response.Failure -> {
                        sellOperationState = OperationState.Error(
                            result.e.message ?: "Failed to sell carbon credits"
                        )
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                sellOperationState = OperationState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun withdrawREC(amount: Float, toAddress: String) {
        viewModelScope.launch {
            try {
                withdrawOperationState = OperationState.Loading
                val uid = authRepository.currentUser?.uid ?: return@launch
                
                when (val result = engagementRepository.withdrawREC(uid, amount, toAddress)) {
                    is Response.Success -> {
                        withdrawOperationState = OperationState.Success(result.data?.message ?: "Withdrawal successful")
                        refresh()
                        loadTransactionHistory()
                    }
                    is Response.Failure -> {
                        withdrawOperationState = OperationState.Error(
                            result.e.message ?: "Failed to withdraw REC"
                        )
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                withdrawOperationState = OperationState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun sendREC(toAddress: String, amount: Float, note: String = "") {
        viewModelScope.launch {
            try {
                sendOperationState = OperationState.Loading
                val uid = authRepository.currentUser?.uid ?: return@launch
                
                when (val result = engagementRepository.sendREC(uid, toAddress, amount, note)) {
                    is Response.Success -> {
                        sendOperationState = OperationState.Success("REC sent successfully")
                        refresh()
                        loadTransactionHistory()
                    }
                    is Response.Failure -> {
                        sendOperationState = OperationState.Error(
                            result.e.message ?: "Failed to send REC"
                        )
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                sendOperationState = OperationState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun resetOperationState(operation: String) {
        when (operation) {
            "sell" -> sellOperationState = OperationState.Idle
            "withdraw" -> withdrawOperationState = OperationState.Idle
            "send" -> sendOperationState = OperationState.Idle
        }
    }

    suspend fun submitPickup(draft: PickupRequestDraft): Boolean = try {
        val uid = authRepository.currentUser?.uid ?: "guest"
        withContext(Dispatchers.IO) {
            when (val result = engagementRepository.submitPickupRequest(uid, draft)) {
                is Response.Success -> result.data == true
                else -> false
            }
        }
    } catch (_: Exception) {
        false
    }
}

sealed class OperationState {
    object Idle : OperationState()
    object Loading : OperationState()
    data class Success(val message: String) : OperationState()
    data class Error(val message: String) : OperationState()
}
