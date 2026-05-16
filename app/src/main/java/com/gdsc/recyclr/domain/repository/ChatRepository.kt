package com.gdsc.recyclr.domain.repository

import android.net.Uri
import com.gdsc.recyclr.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getMessages(threadId: String): Flow<List<ChatMessage>>
    suspend fun sendMessage(threadId: String, message: ChatMessage): Result<Boolean>
    fun getTypingStatuses(threadId: String): Flow<Map<String, String>>
    suspend fun setTypingStatus(threadId: String, userId: String, status: String)
    suspend fun uploadFile(threadId: String, uri: Uri, mimeType: String): Result<String>
}
