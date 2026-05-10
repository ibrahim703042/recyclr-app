package com.gdsc.recyclr.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_records")
data class CachedScanRecordEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val itemType: String,
    val pointsEarned: Int,
    val co2SavedGrams: Float,
    val destination: String,
    val timestampMillis: Long
)

