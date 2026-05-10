package com.gdsc.recyclr.data.location

import com.gdsc.recyclr.domain.model.CollectionPoint

interface GeofenceManager {
    fun register(points: List<CollectionPoint>)
    fun unregisterAll()
}

