package com.gdsc.recyclr.data.location

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.PendingIntentCompat
import com.gdsc.recyclr.domain.model.CollectionPoint
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeofenceManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : GeofenceManager {

    private val geofencingClient: GeofencingClient =
        LocationServices.getGeofencingClient(context)

    private val pendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceReceiver::class.java)
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun register(points: List<CollectionPoint>) {
        if (points.isEmpty()) return

        val geofences = points.take(20).map { p ->
            Geofence.Builder()
                .setRequestId(p.id)
                .setCircularRegion(p.lat, p.lng, 100f)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build()
        }

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geofences)
            .build()

        try {
            geofencingClient.addGeofences(request, pendingIntent)
        } catch (e: SecurityException) {
            // Silently fail if permission was revoked or not yet granted
        }
    }

    override fun unregisterAll() {
        try {
            geofencingClient.removeGeofences(pendingIntent)
        } catch (e: SecurityException) {
            // Silently fail
        }
    }
}

