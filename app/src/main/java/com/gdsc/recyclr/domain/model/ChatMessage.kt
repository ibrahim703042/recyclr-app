package com.gdsc.recyclr.domain.model

enum class ChatMessageType {
    TEXT, IMAGE, AUDIO, VIDEO
}

/** Message chat support or peer-to-peer. */
data class ChatMessage(
    val id: String = "",
    val threadId: String = "",
    val senderId: String = "",
    val body: String = "",
    val fromUser: Boolean = true,
    val sentAtMillis: Long = 0L,
    val type: ChatMessageType = ChatMessageType.TEXT,
    val fileUrl: String? = null,
    val durationMillis: Long? = null,
    val senderName: String? = null,
)
