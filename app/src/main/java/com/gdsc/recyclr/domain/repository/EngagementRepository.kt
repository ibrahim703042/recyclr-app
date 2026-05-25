package com.gdsc.recyclr.domain.repository

import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.engagement.CommunityPost
import com.gdsc.recyclr.domain.model.engagement.DonationCause
import com.gdsc.recyclr.domain.model.engagement.HomeDashboard
import com.gdsc.recyclr.domain.model.engagement.LeaderboardEntry
import com.gdsc.recyclr.domain.model.engagement.PickupRequestDraft
import com.gdsc.recyclr.domain.model.engagement.RecWallet
import com.gdsc.recyclr.domain.model.engagement.WeeklyChallenge

interface EngagementRepository {
    suspend fun getHomeDashboard(userId: String, pointsBalance: Int): Response<HomeDashboard>
    suspend fun getLeaderboard(userId: String): Response<List<LeaderboardEntry>>
    suspend fun getChallenge(userId: String): Response<WeeklyChallenge>
    suspend fun getCommunityFeed(userId: String): Response<List<CommunityPost>>
    suspend fun getWallet(userId: String, pointsBalance: Int): Response<RecWallet>
    suspend fun getDonationCauses(userId: String): Response<List<DonationCause>>
    suspend fun submitPickupRequest(userId: String, draft: PickupRequestDraft): Response<Boolean>
    suspend fun lookupBarcode(barcode: String, userId: String): Response<Pair<String, Int>?>
    suspend fun createCommunityPost(post: CommunityPost): Response<String>
    suspend fun deleteCommunityPost(postId: String): Response<Boolean>
    suspend fun getAdminStats(): Response<Map<String, Long>>
    
    // Wallet operations
    suspend fun getTransactionHistory(
        userId: String,
        limit: Int = 50
    ): Response<List<com.gdsc.recyclr.domain.model.engagement.WalletTransaction>>
    
    suspend fun sellCarbonCredits(
        userId: String,
        amount: Float
    ): Response<com.gdsc.recyclr.domain.model.engagement.WithdrawalResult>
    
    suspend fun withdrawREC(
        userId: String,
        amount: Float,
        toAddress: String
    ): Response<com.gdsc.recyclr.domain.model.engagement.WithdrawalResult>
    
    suspend fun sendREC(
        userId: String,
        toAddress: String,
        amount: Float,
        note: String = ""
    ): Response<com.gdsc.recyclr.domain.model.engagement.WalletTransaction>
    
    suspend fun validateWalletAddress(
        address: String
    ): Response<Boolean>
}
