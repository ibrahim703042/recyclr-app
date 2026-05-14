package com.gdsc.recyclr.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_notifications")
data class CachedNotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val body: String,
    val createdAtMillis: Long,
    val isRead: Boolean = false,
)
