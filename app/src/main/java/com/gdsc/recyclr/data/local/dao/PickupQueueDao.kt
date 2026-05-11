package com.gdsc.recyclr.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gdsc.recyclr.data.local.entities.LocalPickupRequestEntity

@Dao
interface PickupQueueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: LocalPickupRequestEntity)

    @Query("SELECT * FROM pickup_requests_local WHERE synced = 0 ORDER BY createdAtMillis ASC")
    suspend fun pending(): List<LocalPickupRequestEntity>

    @Query("UPDATE pickup_requests_local SET synced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)
}
