package com.gdsc.recyclr.data.repository

import com.gdsc.recyclr.data.local.dao.RedemptionDao
import com.gdsc.recyclr.data.local.entities.CachedRedemptionEntity
import com.gdsc.recyclr.data.model.ShopItemDto
import com.gdsc.recyclr.data.service.RedemptionService
import com.gdsc.recyclr.domain.model.Redemption
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.ShopItem
import com.gdsc.recyclr.domain.repository.RedemptionRepository
import com.gdsc.recyclr.domain.repository.ImpactRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RedemptionRepositoryImpl @Inject constructor(
    private val service: RedemptionService,
    private val dao: RedemptionDao,
    private val impactRepository: ImpactRepository,
) : RedemptionRepository {
    override suspend fun redeem(userId: String, item: ShopItem): Response<Redemption> {
        if (userId == "guest") {
            val id = java.util.UUID.randomUUID().toString()
            val redemption = Redemption(
                id = id,
                userId = userId,
                shopItemId = item.id,
                shopItemTitle = item.title,
                pointsCost = item.price,
                timestampMillis = System.currentTimeMillis(),
                qrPayload = "RECYCLR:guest:$id:${item.id}:${item.price}",
                status = "completed",
                pickupHint = null
            )
            dao.upsert(redemption.toCached())
            impactRepository.applyLocalPointsDelta(userId, -item.price)
            return Response.Success(redemption)
        }

        return service.redeem(userId, ShopItemDto.fromDomain(item))
            .fold(
                onSuccess = { Response.Success(it.toDomain()) },
                onFailure = { Response.Failure(it as Exception) }
            )
    }

    override suspend fun getHistory(userId: String, limit: Long): Response<List<Redemption>> {
        if (userId == "guest") {
            val cached = dao.history(userId, limit.toInt()).map { it.toDomain() }
            return Response.Success(cached)
        }

        return service.getHistory(userId, limit)
            .fold(
                onSuccess = { Response.Success(it.map { dto -> dto.toDomain() }) },
                onFailure = {
                    val cached = dao.history(userId, limit.toInt()).map { it.toDomain() }
                    if (cached.isNotEmpty()) Response.Success(cached) else Response.Failure(it as Exception)
                }
            )
    }

    private fun Redemption.toCached() = CachedRedemptionEntity(
        id = id,
        userId = userId,
        shopItemId = shopItemId,
        shopItemTitle = shopItemTitle,
        pointsCost = pointsCost,
        timestampMillis = timestampMillis,
        qrPayload = qrPayload
    )

    private fun CachedRedemptionEntity.toDomain() = Redemption(
        id = id,
        userId = userId,
        shopItemId = shopItemId,
        shopItemTitle = shopItemTitle,
        pointsCost = pointsCost,
        timestampMillis = timestampMillis,
        qrPayload = qrPayload,
        status = "completed",
        pickupHint = null
    )
}

