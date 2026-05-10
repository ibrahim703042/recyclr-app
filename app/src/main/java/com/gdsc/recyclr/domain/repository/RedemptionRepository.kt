package com.gdsc.recyclr.domain.repository

import com.gdsc.recyclr.domain.model.Redemption
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.ShopItem

typealias RedeemResponse = Response<Redemption>
typealias RedemptionHistoryResponse = Response<List<Redemption>>

interface RedemptionRepository {
    suspend fun redeem(userId: String, item: ShopItem): RedeemResponse
    suspend fun getHistory(userId: String, limit: Long = 50): RedemptionHistoryResponse
}

