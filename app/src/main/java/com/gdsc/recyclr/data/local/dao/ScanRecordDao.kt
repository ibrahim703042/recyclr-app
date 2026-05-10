package com.gdsc.recyclr.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gdsc.recyclr.data.local.entities.CachedScanRecordEntity

@Dao
interface ScanRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(record: CachedScanRecordEntity)

    @Query("SELECT * FROM scan_records WHERE userId = :userId ORDER BY timestampMillis DESC LIMIT :limit")
    suspend fun recent(userId: String, limit: Int): List<CachedScanRecordEntity>
}

