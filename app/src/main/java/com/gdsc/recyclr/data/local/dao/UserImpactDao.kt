package com.gdsc.recyclr.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gdsc.recyclr.data.local.entities.CachedUserImpactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserImpactDao {
    @Query("SELECT * FROM user_impact WHERE userId = :userId LIMIT 1")
    fun observe(userId: String): Flow<CachedUserImpactEntity?>

    @Query("SELECT * FROM user_impact WHERE userId = :userId LIMIT 1")
    suspend fun get(userId: String): CachedUserImpactEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CachedUserImpactEntity)
}

