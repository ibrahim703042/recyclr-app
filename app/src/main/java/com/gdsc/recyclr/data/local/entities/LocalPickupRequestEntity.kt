package com.gdsc.recyclr.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pickup_requests_local")
data class LocalPickupRequestEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val address: String,
    val itemsCsv: String,
    val estimatedKg: Float,
    val repeatEveryWeeks: Int?,
    val createdAtMillis: Long,
    /** false until successfully uploaded to Firestore */
    val synced: Boolean,
)
