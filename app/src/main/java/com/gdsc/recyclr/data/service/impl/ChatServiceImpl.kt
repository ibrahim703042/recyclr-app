package com.gdsc.recyclr.data.service.impl

import com.gdsc.recyclr.data.service.ChatService
import com.gdsc.recyclr.domain.model.ChatMessage
import com.gdsc.recyclr.domain.model.ChatMessageType
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatServiceImpl @Inject constructor(
    private val db: FirebaseDatabase
) : ChatService {

    override fun observeMessages(threadId: String): Flow<List<ChatMessage>> = callbackFlow {
        val ref = db.getReference("chats").child(threadId).child("messages")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull { child ->
                    val map = child.value as? Map<*, *> ?: return@mapNotNull null
                    ChatMessage(
                        id = child.key ?: "",
                        threadId = threadId,
                        senderId = map["senderId"] as? String ?: "",
                        body = map["body"] as? String ?: "",
                        fromUser = map["fromUser"] as? Boolean ?: true,
                        sentAtMillis = map["sentAtMillis"] as? Long ?: 0L,
                        type = try { ChatMessageType.valueOf(map["type"] as? String ?: "TEXT") } catch (e: Exception) { ChatMessageType.TEXT },
                        fileUrl = map["fileUrl"] as? String,
                        durationMillis = map["durationMillis"] as? Long,
                        senderName = map["senderName"] as? String
                    )
                }
                trySend(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override suspend fun sendMessage(threadId: String, message: ChatMessage): Result<Boolean> {
        return try {
            val ref = db.getReference("chats").child(threadId).child("messages").push()
            val data = mapOf(
                "senderId" to message.senderId,
                "body" to message.body,
                "fromUser" to message.fromUser,
                "sentAtMillis" to System.currentTimeMillis(),
                "type" to message.type.name,
                "fileUrl" to message.fileUrl,
                "durationMillis" to message.durationMillis,
                "senderName" to message.senderName
            )
            ref.setValue(data).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeTypingStatus(threadId: String): Flow<Map<String, String>> = callbackFlow {
        val ref = db.getReference("chats").child(threadId).child("status")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val statuses = snapshot.children.associate { child ->
                    (child.key ?: "") to (child.value as? String ?: "none")
                }
                trySend(statuses)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override suspend fun setTypingStatus(threadId: String, userId: String, status: String) {
        try {
            val ref = db.getReference("chats").child(threadId).child("status").child(userId)
            if (status == "none") {
                ref.removeValue().await()
            } else {
                ref.setValue(status).await()
            }
        } catch (e: Exception) {
            // Log error
        }
    }
}
