package com.gdsc.recyclr.data.model

import com.gdsc.recyclr.domain.model.ScanRecord

data class ScanRecordDto(
    val id: String = "",
    val userId: String = "",
    val itemType: String = "",
    val pointsEarned: Int = 0,
    val co2SavedGrams: Float = 0f,
    val destination: String = "",
    val timestampMillis: Long = 0L,
    val scanSource: String = "manual",
    val detectionConfidence: Float? = null,
    val notes: String? = null
) {
    fun toDomain(): ScanRecord = ScanRecord(
        id = id,
        userId = userId,
        itemType = itemType,
        pointsEarned = pointsEarned,
        co2SavedGrams = co2SavedGrams,
        destination = destination,
        timestampMillis = timestampMillis,
        scanSource = scanSource,
        detectionConfidence = detectionConfidence,
        notes = notes
    )

    companion object {
        fun fromDomain(model: ScanRecord): ScanRecordDto = ScanRecordDto(
            id = model.id,
            userId = model.userId,
            itemType = model.itemType,
            pointsEarned = model.pointsEarned,
            co2SavedGrams = model.co2SavedGrams,
            destination = model.destination,
            timestampMillis = model.timestampMillis,
            scanSource = model.scanSource,
            detectionConfidence = model.detectionConfidence,
            notes = model.notes
        )
    }
}
