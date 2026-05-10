package com.gdsc.recyclr.data.service

import com.gdsc.recyclr.data.model.UserImpactDto

interface ImpactService {
    suspend fun getUserImpact(userId: String): Result<UserImpactDto?>
    suspend fun upsertUserImpact(impact: UserImpactDto): Result<Boolean>
}

