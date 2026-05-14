package com.gdsc.recyclr.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.badgeDataStore by preferencesDataStore(name = "recyclr_badges")

/**
 * Repère jusqu’où l’utilisateur a lu le fil support (messages agent avec `sentAtMillis` plus récent = non lus).
 */
@Singleton
class BadgePreferencesStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val lastSupportSeenKey = longPreferencesKey("last_support_thread_seen_millis")

    val lastSupportThreadSeenMillis: Flow<Long> = context.badgeDataStore.data.map { prefs ->
        prefs[lastSupportSeenKey] ?: 0L
    }

    suspend fun markSupportThreadSeenUpTo(millis: Long) {
        context.badgeDataStore.edit { prefs ->
            val current = prefs[lastSupportSeenKey] ?: 0L
            prefs[lastSupportSeenKey] = maxOf(current, millis)
        }
    }

    suspend fun markSupportThreadSeenNow() {
        markSupportThreadSeenUpTo(System.currentTimeMillis())
    }
}
