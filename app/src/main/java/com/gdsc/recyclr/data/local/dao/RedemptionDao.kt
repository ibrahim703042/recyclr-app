package com.gdsc.recyclr.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gdsc.recyclr.data.local.entities.CachedRedemptionEntity

@Dao
interface RedemptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CachedRedemptionEntity)

    @Query("SELECT * FROM redemptions WHERE userId = :userId ORDER BY timestampMillis DESC LIMIT :limit")
    suspend fun history(userId: String, limit: Int): List<CachedRedemptionEntity>
}

