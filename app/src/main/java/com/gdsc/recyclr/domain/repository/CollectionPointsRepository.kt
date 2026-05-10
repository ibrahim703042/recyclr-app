package com.gdsc.recyclr.domain.repository

import com.gdsc.recyclr.domain.model.CollectionPoint
import com.gdsc.recyclr.domain.model.Response

typealias CollectionPointsResponse = Response<List<CollectionPoint>>

interface CollectionPointsRepository {
    suspend fun getNearbyCollectionPoints(
        lat: Double,
        lng: Double,
        radiusKm: Double,
        typeFilter: Set<String> = emptySet()
    ): CollectionPointsResponse
}

