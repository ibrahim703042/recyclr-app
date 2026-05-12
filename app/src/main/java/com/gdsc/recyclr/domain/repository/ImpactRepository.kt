package com.gdsc.recyclr.domain.repository

import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.UserImpact
import kotlinx.coroutines.flow.Flow

typealias UserImpactResponse = Response<UserImpact>

interface ImpactRepository {
    fun observeUserImpact(userId: String): Flow<UserImpactResponse>
    suspend fun getUserImpact(userId: String): UserImpactResponse
    suspend fun updateImpactAfterScan(
        userId: String,
        pointsDelta: Int,
        co2SavedGramsDelta: Float,
        wasteDivertedKgDelta: Float,
        energyRecoveredKwhDelta: Float,
        treesEquivalentDelta: Int
    ): Response<Boolean>
}

