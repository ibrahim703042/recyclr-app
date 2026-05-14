package com.gdsc.recyclr.domain.model

/**
 * Modèles domain alignés README V4 / Firestore (référence & futures sync).
 * Les écrans existants utilisent surtout Dtos + services ; ces types unifient le vocabulaire métier.
 */
data class TokenTransaction(
    val id: String,
    val userId: String,
    val amount: Double,
    val type: String,
    val referenceId: String,
    val timestampMillis: Long,
    val txHash: String? = null,
)

data class CarbonCreditRecord(
    val id: String,
    val userId: String,
    val amountTonnes: Double,
    val verified: Boolean,
    val batchId: String? = null,
    val status: String,
    val salePriceUsd: Double? = null,
)

data class CommunityPostRecord(
    val id: String,
    val userId: String,
    val groupId: String,
    val content: String,
    val imageUrl: String? = null,
    val type: String,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val status: String,
    val createdAtMillis: Long,
)

data class PostLike(
    val postId: String,
    val userId: String,
    val timestampMillis: Long,
)

data class PostComment(
    val id: String,
    val postId: String,
    val userId: String,
    val content: String,
    val createdAtMillis: Long,
)

data class UserGroupMembership(
    val userId: String,
    val groupId: String,
    val joinedAtMillis: Long,
    val role: String,
)

data class UserBadgeGrant(
    val userId: String,
    val badgeId: String,
    val earnedAtMillis: Long,
)

data class WeeklyChallengeRecord(
    val id: String,
    val title: String,
    val description: String,
    val target: Int,
    val pointsReward: Int,
    val startDateMillis: Long,
    val endDateMillis: Long,
    val active: Boolean,
)

data class XrplAccountRecord(
    val userId: String,
    val address: String,
    val publicKey: String,
    val isCustodial: Boolean,
    val encryptedPrivateKey: String? = null,
)
