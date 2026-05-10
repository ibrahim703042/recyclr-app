package com.gdsc.recyclr.data.repository

import com.gdsc.recyclr.data.local.dao.ShopItemDao
import com.gdsc.recyclr.data.local.entities.CachedShopItemEntity
import com.gdsc.recyclr.data.service.ShopService
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.ShopItem
import com.gdsc.recyclr.domain.repository.ShopRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository implementation for Shop
 * Coordinates between domain layer and data service layer
 */
@Singleton
class ShopRepositoryImpl @Inject constructor(
    private val shopService: ShopService,
    private val shopItemDao: ShopItemDao
) : ShopRepository {
    
    override suspend fun getAllShopItems(): Response<List<ShopItem>> {
        return shopService.getAllShopItems().fold(
            onSuccess = { dtos ->
                val domain = dtos.map { it.toDomain() }.filter { it.isActive }
                shopItemDao.upsertAll(domain.map { it.toCached() })
                Response.Success(domain)
            },
            onFailure = {
                val cached = shopItemDao.all().map { it.toDomain() }.filter { it.isActive }
                if (cached.isNotEmpty()) Response.Success(cached) else Response.Failure(it as Exception)
            }
        )
    }

    private fun ShopItem.toCached() = CachedShopItemEntity(
        id = id,
        title = title,
        price = price,
        description = description,
        category = category,
        imageUrl = imageUrl
    )

    private fun CachedShopItemEntity.toDomain() = ShopItem(
        id = id,
        title = title,
        price = price,
        description = description,
        category = category,
        imageUrl = imageUrl,
        isActive = true,
        stockQuantity = null,
        termsSummary = null
    )
}
