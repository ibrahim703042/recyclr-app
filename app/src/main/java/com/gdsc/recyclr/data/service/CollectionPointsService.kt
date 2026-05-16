package com.gdsc.recyclr.data.service

import com.gdsc.recyclr.data.model.CollectionPointDto
import com.gdsc.recyclr.data.model.CollectorLocationDto
import kotlinx.coroutines.flow.Flow

interface CollectionPointsService {
    suspend fun getAllCollectionPoints(): Result<List<CollectionPointDto>>
    fun observeCollectors(): Flow<List<CollectorLocationDto>>
    suspend fun updateCollectorLocation(collectorId: String, lat: Double, lng: Double): Result<Boolean>
}

