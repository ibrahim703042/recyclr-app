package com.gdsc.recyclr.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class CachedChatMessageEntity(
    @PrimaryKey val id: String,
    val threadId: String,
    val body: String,
    val fromUser: Boolean,
    val sentAtMillis: Long,
)
