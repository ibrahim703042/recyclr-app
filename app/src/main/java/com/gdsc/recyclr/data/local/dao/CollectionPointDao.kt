package com.gdsc.recyclr.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gdsc.recyclr.data.local.entities.CachedCollectionPointEntity

@Dao
interface CollectionPointDao {
    @Query("SELECT * FROM collection_points")
    suspend fun all(): List<CachedCollectionPointEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CachedCollectionPointEntity>)
}

