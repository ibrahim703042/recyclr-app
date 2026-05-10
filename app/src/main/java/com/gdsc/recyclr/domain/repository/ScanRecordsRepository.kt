package com.gdsc.recyclr.domain.repository

import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.ScanRecord

typealias SaveScanRecordResponse = Response<Boolean>
typealias ScanHistoryResponse = Response<List<ScanRecord>>

interface ScanRecordsRepository {
    suspend fun saveScanRecord(record: ScanRecord): SaveScanRecordResponse
    suspend fun getRecentScans(userId: String, limit: Long = 50): ScanHistoryResponse
}

