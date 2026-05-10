package com.gdsc.recyclr.domain.model

data class Redemption(
    val id: String,
    val userId: String,
    val shopItemId: String,
    val shopItemTitle: String,
    val pointsCost: Int,
    val timestampMillis: Long,
    val qrPayload: String,
    val status: String = "completed",
    val pickupHint: String? = null
)
