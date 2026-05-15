package com.gdsc.recyclr.data.repository

import com.gdsc.recyclr.data.local.dao.CollectionPointDao
import com.gdsc.recyclr.data.local.entities.CachedCollectionPointEntity
import com.gdsc.recyclr.data.model.CollectorLocationDto
import com.gdsc.recyclr.data.service.CollectionPointsService
import com.gdsc.recyclr.domain.model.CollectionPoint
import com.gdsc.recyclr.domain.model.CollectorLocation
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.repository.CollectionPointsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*

@Singleton
class CollectionPointsRepositoryImpl @Inject constructor(
    private val service: CollectionPointsService,
    private val dao: CollectionPointDao
) : CollectionPointsRepository {

    override suspend fun getNearbyCollectionPoints(
        lat: Double,
        lng: Double,
        radiusKm: Double,
        typeFilter: Set<String>
    ): Response<List<CollectionPoint>> {
        return service.getAllCollectionPoints()
            .fold(
                onSuccess = { dtos ->
                    val points = dtos.map { it.toDomain() }
                    dao.upsertAll(points.map { it.toCached() })
                    val filtered = points.filter { p ->
                        (typeFilter.isEmpty() || p.types.any { it in typeFilter }) &&
                            haversineKm(lat, lng, p.lat, p.lng) <= radiusKm
                    }
                    Response.Success(filtered)
                },
                onFailure = {
                    val cached = dao.all().map { it.toDomain() }
                    val filtered = cached.filter { p ->
                        (typeFilter.isEmpty() || p.types.any { it in typeFilter }) &&
                            haversineKm(lat, lng, p.lat, p.lng) <= radiusKm
                    }
                    if (filtered.isNotEmpty()) Response.Success(filtered) else Response.Failure(it as Exception)
                }
            )
    }

    override fun observeNearbyCollectors(): Flow<List<CollectorLocation>> {
        return service.observeCollectors().map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    private fun CollectorLocationDto.toDomain() = CollectorLocation(
        id = collectorId,
        name = name,
        lat = lat,
        lng = lng,
        vehicleType = vehicleType
    )

    // Small, deterministic distance check without extra dependencies
    private fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    private fun CollectionPoint.toCached() = CachedCollectionPointEntity(
        id = id,
        name = name,
        lat = lat,
        lng = lng,
        typesCsv = types.joinToString(","),
        hours = hours,
        phone = phone
    )

    private fun CachedCollectionPointEntity.toDomain() = CollectionPoint(
        id = id,
        name = name,
        lat = lat,
        lng = lng,
        types = typesCsv.split(",").map { it.trim() }.filter { it.isNotBlank() },
        hours = hours,
        phone = phone,
        address = "",
        websiteUrl = null,
        accessibilityNote = null
    )
}

