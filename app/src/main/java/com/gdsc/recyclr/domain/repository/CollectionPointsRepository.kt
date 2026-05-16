package com.gdsc.recyclr.domain.repository

import com.gdsc.recyclr.domain.model.CollectionPoint
import com.gdsc.recyclr.domain.model.CollectorLocation
import com.gdsc.recyclr.domain.model.Response
import kotlinx.coroutines.flow.Flow

typealias CollectionPointsResponse = Response<List<CollectionPoint>>

interface CollectionPointsRepository {
    suspend fun getNearbyCollectionPoints(
        lat: Double,
        lng: Double,
        radiusKm: Double,
        typeFilter: Set<String> = emptySet()
    ): CollectionPointsResponse

    fun observeNearbyCollectors(): Flow<List<CollectorLocation>>
    suspend fun updateCollectorLocation(collectorId: String, lat: Double, lng: Double): Response<Boolean>
}

