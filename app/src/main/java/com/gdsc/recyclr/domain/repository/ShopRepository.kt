package com.gdsc.recyclr.domain.repository

import com.gdsc.recyclr.domain.model.ShopItem
import com.gdsc.recyclr.domain.model.Response

interface ShopRepository {
    suspend fun getAllShopItems(): Response<List<ShopItem>>
}
