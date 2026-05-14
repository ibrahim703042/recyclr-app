package com.gdsc.recyclr.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gdsc.recyclr.data.local.entities.CachedNotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM app_notifications ORDER BY createdAtMillis DESC")
    fun observeAll(): Flow<List<CachedNotificationEntity>>

    @Query("SELECT COUNT(*) FROM app_notifications")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CachedNotificationEntity>)

    @Update
    suspend fun update(item: CachedNotificationEntity)

    @Query("UPDATE app_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markRead(id: String)
}
