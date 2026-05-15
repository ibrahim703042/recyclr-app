package com.gdsc.recyclr.domain.model.engagement

import com.gdsc.recyclr.domain.model.UserRole

data class EcoStreak(
    val currentDays: Int,
    val bestDays: Int,
    val multiplier: Float,
    val bonusPointsToday: Int,
)

data class WeeklyChallenge(
    val id: String,
    val title: String,
    val description: String,
    val targetScans: Int,
    val currentScans: Int,
    val rewardPoints: Int,
    val endsInDays: Int,
)

data class LeaderboardEntry(
    val userId: String = "",
    val rank: Int,
    val name: String,
    val points: Int,
    val isCurrentUser: Boolean = false,
    val role: UserRole = UserRole.USER,
)

data class CommunityPost(
    val id: String,
    val author: String,
    val groupName: String,
    val message: String,
    val likes: Int,
    val isReport: Boolean = false,
    /** Server time for relative labels (e.g. “2h ago”); optional for legacy seeds. */
    val createdAtMillis: Long? = null,
)

data class RecWallet(
    val recBalance: Double,
    val pointsBalance: Int,
    val conversionRate: Int,
    val carbonCreditsTonnes: Float,
    val lifetimeRecMinted: Double,
)

data class DonationCause(
    val id: String,
    val title: String,
    val description: String,
    val pointsCost: Int,
    val category: String,
)

data class PickupRequestDraft(
    val address: String,
    val itemTypes: List<String>,
    val estimatedKg: Float,
    val repeatEveryWeeks: Int?,
)

data class UserBadge(
    val id: String,
    val title: String,
    val description: String,
    val earned: Boolean,
)

data class HomeDashboard(
    val streak: EcoStreak,
    val challenge: WeeklyChallenge,
    val leaderboard: List<LeaderboardEntry>,
    /** Publications récentes (ex. cette semaine) — défilant en carrousel sur l’accueil. */
    val communityHighlights: List<CommunityPost>,
    val wallet: RecWallet,
    val badges: List<UserBadge>,
)
