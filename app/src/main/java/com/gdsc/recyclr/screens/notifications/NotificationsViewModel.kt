package com.gdsc.recyclr.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdsc.recyclr.data.local.dao.NotificationDao
import com.gdsc.recyclr.data.local.entities.CachedNotificationEntity
import com.gdsc.recyclr.domain.model.AppNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationDao: NotificationDao,
) : ViewModel() {

    val notifications = notificationDao.observeAll()
        .map { list ->
            list.map { e ->
                AppNotification(
                    id = e.id,
                    title = e.title,
                    body = e.body,
                    createdAtMillis = e.createdAtMillis,
                    read = e.isRead,
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch { seedIfEmpty() }
    }

    private suspend fun seedIfEmpty() {
        if (notificationDao.count() > 0) return
        val now = System.currentTimeMillis()
        notificationDao.insertAll(
            listOf(
                CachedNotificationEntity(
                    id = "seed_challenge",
                    title = "Challenge completed",
                    body = "You earned bonus points for this week’s goal.",
                    createdAtMillis = now - 86_400_000L,
                    isRead = false,
                ),
                CachedNotificationEntity(
                    id = "seed_friend",
                    title = "Friend joined Recyclr",
                    body = "Invite more friends to climb the leaderboard.",
                    createdAtMillis = now - 172_800_000L,
                    isRead = false,
                ),
            ),
        )
    }

    fun markRead(id: String) {
        viewModelScope.launch { notificationDao.markRead(id) }
    }

    fun markAllRead() {
        viewModelScope.launch { notificationDao.markAllRead() }
    }
}
