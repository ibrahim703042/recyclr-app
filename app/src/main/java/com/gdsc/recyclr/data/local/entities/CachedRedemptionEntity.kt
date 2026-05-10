package com.gdsc.recyclr.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "redemptions")
data class CachedRedemptionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val shopItemId: String,
    val shopItemTitle: String,
    val pointsCost: Int,
    val timestampMillis: Long,
    val qrPayload: String
)

