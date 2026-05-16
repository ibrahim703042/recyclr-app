package com.gdsc.recyclr.screens.support

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdsc.recyclr.data.local.preferences.BadgePreferencesStore
import com.gdsc.recyclr.domain.model.ChatMessage
import com.gdsc.recyclr.domain.model.ChatMessageType
import com.gdsc.recyclr.domain.repository.AuthRepository
import com.gdsc.recyclr.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SupportChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,
    private val badgePreferencesStore: BadgePreferencesStore,
) : ViewModel() {

    private val currentUserId = authRepository.currentUser?.uid ?: "guest"

    // Each user's support thread is keyed by their own UID
    private val threadId = currentUserId

    val messages: StateFlow<List<ChatMessage>> = chatRepository
        .getMessages(threadId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val typingStatuses: StateFlow<Map<String, String>> = chatRepository
        .getTypingStatuses(threadId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    fun sendUserMessage(text: String) {
        if (text.isBlank() || currentUserId == "guest") return
        viewModelScope.launch {
            setTypingStatus("none")
            chatRepository.sendMessage(
                threadId,
                ChatMessage(
                    senderId = currentUserId,
                    body = text.trim(),
                    fromUser = true,
                    type = ChatMessageType.TEXT,
                )
            )
        }
    }

    fun sendMediaMessage(
        uri: Uri,
        mimeType: String,
        type: ChatMessageType,
        durationMillis: Long? = null,
    ) {
        if (currentUserId == "guest") return
        viewModelScope.launch {
            setTypingStatus("none")
            chatRepository.uploadFile(threadId, uri, mimeType)
                .onSuccess { url ->
                    chatRepository.sendMessage(
                        threadId,
                        ChatMessage(
                            senderId = currentUserId,
                            fromUser = true,
                            type = type,
                            fileUrl = url,
                            durationMillis = durationMillis,
                        )
                    )
                }
                .onFailure { /* TODO: show snackbar */ }
        }
    }

    fun setTypingStatus(status: String) {
        if (currentUserId == "guest") return
        viewModelScope.launch {
            chatRepository.setTypingStatus(threadId, currentUserId, status)
        }
    }

    fun markSupportThreadSeenOnLeave() {
        viewModelScope.launch {
            badgePreferencesStore.markSupportThreadSeenNow()
            setTypingStatus("none")
        }
    }
}
