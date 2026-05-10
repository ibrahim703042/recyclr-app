package com.gdsc.recyclr.data.service

import com.gdsc.recyclr.data.model.RedemptionDto
import com.gdsc.recyclr.data.model.ShopItemDto

interface RedemptionService {
    suspend fun redeem(userId: String, item: ShopItemDto): Result<RedemptionDto>
    suspend fun getHistory(userId: String, limit: Long): Result<List<RedemptionDto>>
}

