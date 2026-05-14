package com.gdsc.recyclr.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat

object RecyclrNotificationChannels {
    const val GENERAL_ID = "recyclr_general"
    const val GENERAL_NAME = "Recyclr"

    fun ensureCreated(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            GENERAL_ID,
            GENERAL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "Challenges, collecte, support et rappels Recyclr."
        }
        nm.createNotificationChannel(channel)
    }

    fun areNotificationsEnabled(context: Context): Boolean =
        NotificationManagerCompat.from(context).areNotificationsEnabled()
}
