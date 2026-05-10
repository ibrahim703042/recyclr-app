package com.gdsc.recyclr.domain.model

data class ScanRecord(
    val id: String,
    val userId: String,
    val itemType: String,
    val pointsEarned: Int,
    val co2SavedGrams: Float,
    val destination: String,
    val timestampMillis: Long,
    /** camera | manual | ml */
    val scanSource: String = "manual",
    val detectionConfidence: Float? = null,
    val notes: String? = null
)
