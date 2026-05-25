package com.gdsc.recyclr.domain.model.engagement

data class WalletTransaction(
    val id: String = "",
    val userId: String = "",
    val type: TransactionType = TransactionType.SCAN_REWARD,
    val amount: Float = 0f,
    val currency: Currency = Currency.REC,
    val status: TransactionStatus = TransactionStatus.PENDING,
    val fromAddress: String = "",
    val toAddress: String = "",
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val metadata: Map<String, String> = emptyMap()
)

enum class TransactionType {
    SCAN_REWARD,
    SHOP_REDEMPTION,
    CARBON_SALE,
    REC_WITHDRAWAL,
    REC_SEND,
    REC_RECEIVE,
    DONATION
}

enum class Currency {
    REC,
    POINTS,
    CARBON_CREDITS
}

enum class TransactionStatus {
    PENDING,
    COMPLETED,
    FAILED,
    CANCELLED
}
