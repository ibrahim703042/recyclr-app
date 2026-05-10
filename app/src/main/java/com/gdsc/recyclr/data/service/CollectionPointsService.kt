package com.gdsc.recyclr.data.service

import com.gdsc.recyclr.data.model.CollectionPointDto

interface CollectionPointsService {
    suspend fun getAllCollectionPoints(): Result<List<CollectionPointDto>>
}

