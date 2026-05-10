package com.gdsc.recyclr.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_impact")
data class CachedUserImpactEntity(
    @PrimaryKey val userId: String,
    val totalScans: Int,
    val wasteDivertedKg: Float,
    val co2SavedKg: Float,
    val energyRecoveredKwh: Float,
    val treesEquivalent: Int,
    val pointsBalance: Int
)

