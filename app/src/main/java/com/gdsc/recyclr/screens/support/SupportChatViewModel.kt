package com.gdsc.recyclr.screens.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdsc.recyclr.data.local.dao.ChatMessageDao
import com.gdsc.recyclr.data.local.entities.CachedChatMessageEntity
import com.gdsc.recyclr.data.local.preferences.BadgePreferencesStore
import com.gdsc.recyclr.domain.model.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SupportChatViewModel @Inject constructor(
    private val chatMessageDao: ChatMessageDao,
    private val badgePreferencesStore: BadgePreferencesStore,
) : ViewModel() {

    val messages = chatMessageDao.observeThread(SUPPORT_CHAT_THREAD_ID)
        .map { rows ->
            rows.map { r ->
                ChatMessage(
                    id = r.id,
                    threadId = r.threadId,
                    body = r.body,
                    fromUser = r.fromUser,
                    sentAtMillis = r.sentAtMillis,
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch { ensureWelcome() }
    }

    private suspend fun ensureWelcome() {
        chatMessageDao.insert(
            CachedChatMessageEntity(
                id = "welcome_seed",
                threadId = SUPPORT_CHAT_THREAD_ID,
                body = "Hello! Ask us about recycling, pickups, or rewards.",
                fromUser = false,
                sentAtMillis = System.currentTimeMillis(),
            ),
        )
    }

    fun sendUserMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            chatMessageDao.insert(
                CachedChatMessageEntity(
                    id = UUID.randomUUID().toString(),
                    threadId = SUPPORT_CHAT_THREAD_ID,
                    body = text.trim(),
                    fromUser = true,
                    sentAtMillis = System.currentTimeMillis(),
                ),
            )
            // Simple canned reply
            chatMessageDao.insert(
                CachedChatMessageEntity(
                    id = UUID.randomUUID().toString(),
                    threadId = SUPPORT_CHAT_THREAD_ID,
                    body = "Thanks for your message. A teammate will follow up soon.",
                    fromUser = false,
                    sentAtMillis = System.currentTimeMillis() + 1,
                ),
            )
        }
    }

    fun markSupportThreadSeenOnLeave() {
        viewModelScope.launch {
            badgePreferencesStore.markSupportThreadSeenNow()
        }
    }
}
