package com.gdsc.recyclr.push

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.gdsc.recyclr.R
import com.gdsc.recyclr.data.local.dao.NotificationDao
import com.gdsc.recyclr.data.local.entities.CachedNotificationEntity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * Messages **data** supportés (côté serveur / console Firebase) :
 * - `title`, `body` : texte affiché (sinon champs [RemoteMessage.notification]).
 * - `type` : `challenge` | `pickup` | `support` | `promo` | `community` (stocké dans l’id pour le filtre).
 */
@AndroidEntryPoint
class RecyclrFirebaseMessagingService : FirebaseMessagingService() {

    @Inject lateinit var notificationDao: NotificationDao

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private fun canPostSystemNotifications(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title
            ?: message.data["title"]
            ?: getString(R.string.push_default_title)
        val body = message.notification?.body
            ?: message.data["body"]
            ?: getString(R.string.push_default_body)
        val type = message.data["type"] ?: "general"
        val id = message.messageId ?: message.data["message_id"] ?: UUID.randomUUID().toString()
        val now = System.currentTimeMillis()

        scope.launch {
            notificationDao.insert(
                CachedNotificationEntity(
                    id = "push_${type}_$id",
                    title = title,
                    body = body,
                    createdAtMillis = now,
                    isRead = false,
                ),
            )
        }

        if (canPostSystemNotifications()) {
            showSystemNotification(title, body, type)
        }
    }

    private fun showSystemNotification(title: String, body: String, type: String) {
        // setClassName évite de référencer MainActivity (annotations expérimentales Compose / Accompanist).
        val intent = Intent().apply {
            setClassName(packageName, "com.gdsc.recyclr.activities.MainActivity")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_OPEN_NOTIFICATIONS, true)
            putExtra(EXTRA_PUSH_TYPE, type)
        }
        val pending = PendingIntent.getActivity(
            this,
            type.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(this, RecyclrNotificationChannels.GENERAL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()

        if (canPostSystemNotifications()) {
            NotificationManagerCompat.from(this).notify(type.hashCode() and 0xFFFF, notification)
        }
    }

    companion object {
        const val EXTRA_OPEN_NOTIFICATIONS = "extra_open_notifications"
        const val EXTRA_PUSH_TYPE = "extra_push_type"
    }
}
