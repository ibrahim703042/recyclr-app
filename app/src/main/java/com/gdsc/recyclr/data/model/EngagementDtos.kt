package com.gdsc.recyclr.data.model

/** Document id = barcode, collection `barcodes`. Fields: itemLabel, points */
data class BarcodeLookupDto(
    val itemLabel: String = "",
    val points: Long = 0,
)

/** Document path `engagement_config/weekly_challenge`. */
data class WeeklyChallengeConfigDto(
    val id: String = "lake-week",
    val title: String = "",
    val description: String = "",
    val targetScans: Long = 40,
    val communityScansCount: Long = 0,
    val rewardPoints: Long = 300,
    val endsInDays: Long = 4,
)

/** Collection `leaderboard`. Fields: rank, displayName, points, isCurrentUser */
data class LeaderboardEntryDto(
    val userId: String = "",
    val rank: Long = 0,
    val displayName: String = "",
    val points: Long = 0,
    val isCurrentUser: Boolean = false,
    val role: String = "USER",
)

/** Collection `community_posts`. Order by createdAtMillis descending. Document id in [id]. */
data class CommunityPostDto(
    val id: String = "",
    val author: String = "",
    val groupName: String = "",
    val message: String = "",
    val likes: Long = 0,
    val isReport: Boolean = false,
    val createdAtMillis: Long = 0,
) {
    companion object {
        fun fromDomain(model: com.gdsc.recyclr.domain.model.engagement.CommunityPost): CommunityPostDto = CommunityPostDto(
            id = model.id,
            author = model.author,
            groupName = model.groupName,
            message = model.message,
            likes = model.likes.toLong(),
            isReport = model.isReport,
            createdAtMillis = model.createdAtMillis ?: System.currentTimeMillis()
        )
    }
}

/** Collection `donation_causes`. Document id used as [id]. */
data class DonationCauseDto(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val pointsCost: Long = 0,
    val category: String = "",
)
