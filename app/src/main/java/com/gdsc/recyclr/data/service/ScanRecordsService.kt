package com.gdsc.recyclr.data.service

import com.gdsc.recyclr.data.model.ScanRecordDto

interface ScanRecordsService {
    suspend fun saveScanRecord(record: ScanRecordDto): Result<Boolean>
    suspend fun getRecentScans(userId: String, limit: Long): Result<List<ScanRecordDto>>
}

