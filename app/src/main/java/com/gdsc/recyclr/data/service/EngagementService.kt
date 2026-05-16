package com.gdsc.recyclr.data.service

import com.gdsc.recyclr.data.model.BarcodeLookupDto
import com.gdsc.recyclr.data.model.CommunityPostDto
import com.gdsc.recyclr.data.model.DonationCauseDto
import com.gdsc.recyclr.data.model.LeaderboardEntryDto
import com.gdsc.recyclr.data.model.WeeklyChallengeConfigDto
import com.gdsc.recyclr.domain.model.engagement.PickupRequestDraft

interface EngagementService {
    suspend fun getBarcode(barcode: String): Result<BarcodeLookupDto?>
    suspend fun getWeeklyChallengeConfig(): Result<WeeklyChallengeConfigDto?>
    suspend fun getLeaderboard(limit: Long = 20): Result<List<LeaderboardEntryDto>>
    suspend fun getCommunityPosts(limit: Long = 30): Result<List<CommunityPostDto>>
    suspend fun getDonationCauses(): Result<List<DonationCauseDto>>
    suspend fun submitPickupRequest(userId: String, draft: PickupRequestDraft): Result<String>
    suspend fun createCommunityPost(post: CommunityPostDto): Result<String>
    suspend fun deleteCommunityPost(postId: String): Result<Boolean>
    suspend fun getAdminStats(): Result<Map<String, Long>>
}
