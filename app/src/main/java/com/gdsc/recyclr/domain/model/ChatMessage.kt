package com.gdsc.recyclr.domain.model

/** Message chat support (thread local). */
data class ChatMessage(
    val id: String,
    val threadId: String,
    val body: String,
    val fromUser: Boolean,
    val sentAtMillis: Long,
)
