package com.gdsc.recyclr.data.model

import com.gdsc.recyclr.domain.model.Redemption

data class RedemptionDto(
    val id: String = "",
    val userId: String = "",
    val shopItemId: String = "",
    val shopItemTitle: String = "",
    val pointsCost: Int = 0,
    val timestampMillis: Long = 0L,
    val qrPayload: String = "",
    val status: String = "completed",
    val pickupHint: String? = null
) {
    fun toDomain(): Redemption = Redemption(
        id = id,
        userId = userId,
        shopItemId = shopItemId,
        shopItemTitle = shopItemTitle,
        pointsCost = pointsCost,
        timestampMillis = timestampMillis,
        qrPayload = qrPayload,
        status = status,
        pickupHint = pickupHint
    )

    companion object {
        fun fromDomain(model: Redemption): RedemptionDto = RedemptionDto(
            id = model.id,
            userId = model.userId,
            shopItemId = model.shopItemId,
            shopItemTitle = model.shopItemTitle,
            pointsCost = model.pointsCost,
            timestampMillis = model.timestampMillis,
            qrPayload = model.qrPayload,
            status = model.status,
            pickupHint = model.pickupHint
        )
    }
}
