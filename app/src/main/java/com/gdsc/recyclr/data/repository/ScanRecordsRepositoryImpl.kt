package com.gdsc.recyclr.data.repository

import android.graphics.Bitmap
import com.gdsc.recyclr.data.local.dao.ScanRecordDao
import com.gdsc.recyclr.data.local.entities.CachedScanRecordEntity
import com.gdsc.recyclr.data.model.ScanRecordDto
import com.gdsc.recyclr.data.service.ScanRecordsService
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.ScanRecord
import com.gdsc.recyclr.domain.repository.SaveScanRecordResponse
import com.gdsc.recyclr.domain.repository.ScanHistoryResponse
import com.gdsc.recyclr.domain.repository.ScanRecordsRepository
import com.gdsc.recyclr.domain.repository.UploadImageResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScanRecordsRepositoryImpl @Inject constructor(
    private val service: ScanRecordsService,
    private val dao: ScanRecordDao
) : ScanRecordsRepository {
    override suspend fun saveScanRecord(record: ScanRecord): SaveScanRecordResponse {
        val dto = ScanRecordDto.fromDomain(record)
        // Always cache locally for guest/offline mode
        dao.upsert(record.toCached())
        if (record.userId == "guest") return Response.Success(true)

        return service.saveScanRecord(dto)
            .fold(
                onSuccess = { Response.Success(it) },
                onFailure = { Response.Failure(it as Exception) }
            )
    }

    override suspend fun getRecentScans(userId: String, limit: Long): ScanHistoryResponse {
        if (userId == "guest") {
            val cached = dao.recent(userId, limit.toInt()).map { it.toDomain() }
            return Response.Success(cached)
        }

        return service.getRecentScans(userId, limit)
            .fold(
                onSuccess = {
                    val domain = it.map { dto -> dto.toDomain() }
                    Response.Success(domain)
                },
                onFailure = {
                    val cached = dao.recent(userId, limit.toInt()).map { it.toDomain() }
                    if (cached.isNotEmpty()) Response.Success(cached) else Response.Failure(it as Exception)
                }
            )
    }

    override suspend fun uploadScanImage(userId: String, scanId: String, bitmap: Bitmap): UploadImageResponse {
        if (userId == "guest") return Response.Failure(Exception("Guest mode does not support image upload"))
        
        return service.uploadScanImage(userId, scanId, bitmap)
            .fold(
                onSuccess = { Response.Success(it) },
                onFailure = { Response.Failure(it as Exception) }
            )
    }

    private fun ScanRecord.toCached() = CachedScanRecordEntity(
        id = id,
        userId = userId,
        itemType = itemType,
        pointsEarned = pointsEarned,
        co2SavedGrams = co2SavedGrams,
        destination = destination,
        timestampMillis = timestampMillis
    )

    private fun CachedScanRecordEntity.toDomain() = ScanRecord(
        id = id,
        userId = userId,
        itemType = itemType,
        pointsEarned = pointsEarned,
        co2SavedGrams = co2SavedGrams,
        destination = destination,
        timestampMillis = timestampMillis,
        scanSource = "manual",
        detectionConfidence = null,
        notes = null
    )
}

