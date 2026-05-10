package com.gdsc.recyclr.data.location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent) ?: return
        if (event.hasError()) {
            return
        }
        if (event.geofenceTransition != com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER) return

        val ids = event.triggeringGeofences?.map { it.requestId }.orEmpty()
        if (ids.isEmpty()) return

        NotificationHelper.showGeofenceNotification(
            context = context,
            title = "Recyclr reminder",
            message = "You're near a collection point. Consider dropping off your items."
        )
    }
}

