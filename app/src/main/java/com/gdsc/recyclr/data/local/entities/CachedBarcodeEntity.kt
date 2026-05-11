package com.gdsc.recyclr.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "barcode_cache")
data class CachedBarcodeEntity(
    @PrimaryKey val barcode: String,
    val itemLabel: String,
    val points: Int,
    val fetchedAtMillis: Long,
)
