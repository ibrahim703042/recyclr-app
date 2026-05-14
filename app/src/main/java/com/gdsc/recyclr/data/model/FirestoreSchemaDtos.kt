package com.gdsc.recyclr.data.model

/** DTO Firestore `token_transactions` */
data class TokenTransactionDto(
    val userId: String = "",
    val amount: Double = 0.0,
    val type: String = "",
    val referenceId: String = "",
    val timestampMillis: Long = 0L,
    val txHash: String? = null,
)

/** DTO `carbon_credits` */
data class CarbonCreditDto(
    val userId: String = "",
    val amountTonnes: Double = 0.0,
    val verified: Boolean = false,
    val batchId: String? = null,
    val status: String = "available",
    val salePriceUsd: Double? = null,
)

/** DTO `posts` */
data class PostFirestoreDto(
    val userId: String = "",
    val groupId: String = "",
    val content: String = "",
    val imageUrl: String? = null,
    val type: String = "normal",
    val likeCount: Long = 0,
    val commentCount: Long = 0,
    val status: String = "active",
    val createdAtMillis: Long = 0L,
)

/** DTO `user_wallet` */
data class UserWalletDto(
    val recBalance: Double = 0.0,
    val carbonCreditsBalance: Double = 0.0,
)
