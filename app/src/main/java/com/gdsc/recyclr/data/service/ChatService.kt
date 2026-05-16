package com.gdsc.recyclr.data.service

import com.gdsc.recyclr.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatService {
    fun observeMessages(threadId: String): Flow<List<ChatMessage>>
    suspend fun sendMessage(threadId: String, message: ChatMessage): Result<Boolean>
    fun observeTypingStatus(threadId: String): Flow<Map<String, String>>
    suspend fun setTypingStatus(threadId: String, userId: String, status: String)
}
