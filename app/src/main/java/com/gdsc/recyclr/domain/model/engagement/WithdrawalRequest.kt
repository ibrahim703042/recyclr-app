package com.gdsc.recyclr.domain.model.engagement

data class WithdrawalRequest(
    val amount: Float,
    val currency: Currency,
    val toAddress: String,
    val userId: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class WithdrawalResult(
    val success: Boolean,
    val transactionId: String = "",
    val message: String = "",
    val estimatedCompletionTime: Long = 0L
)

data class OperationResult(
    val success: Boolean,
    val message: String = "",
    val data: Any? = null
)
