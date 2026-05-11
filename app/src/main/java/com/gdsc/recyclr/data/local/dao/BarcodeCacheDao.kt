package com.gdsc.recyclr.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gdsc.recyclr.data.local.entities.CachedBarcodeEntity

@Dao
interface BarcodeCacheDao {
    @Query("SELECT * FROM barcode_cache WHERE barcode = :barcode LIMIT 1")
    suspend fun get(barcode: String): CachedBarcodeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CachedBarcodeEntity)
}
