package com.gdsc.recyclr.data.repository

import android.net.Uri
import com.gdsc.recyclr.data.service.ChatService
import com.gdsc.recyclr.domain.model.ChatMessage
import com.gdsc.recyclr.domain.repository.ChatRepository
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val service: ChatService,
    private val storage: FirebaseStorage
) : ChatRepository {

    override fun getMessages(threadId: String): Flow<List<ChatMessage>> = service.observeMessages(threadId)

    override suspend fun sendMessage(threadId: String, message: ChatMessage): Result<Boolean> = 
        service.sendMessage(threadId, message)

    override fun getTypingStatuses(threadId: String): Flow<Map<String, String>> = 
        service.observeTypingStatus(threadId)

    override suspend fun setTypingStatus(threadId: String, userId: String, status: String) = 
        service.setTypingStatus(threadId, userId, status)

    override suspend fun uploadFile(threadId: String, uri: Uri, mimeType: String): Result<String> {
        return try {
            val extension = when {
                mimeType.contains("image") -> "jpg"
                mimeType.contains("audio") -> "m4a"
                mimeType.contains("video") -> "mp4"
                else -> "bin"
            }
            val fileName = "${UUID.randomUUID()}.$extension"
            val ref = storage.reference.child("chats/$threadId/$fileName")
            ref.putFile(uri).await()
            val url = ref.downloadUrl.await().toString()
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
