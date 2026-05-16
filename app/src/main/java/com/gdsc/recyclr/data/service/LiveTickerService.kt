package com.gdsc.recyclr.data.service

import kotlinx.coroutines.flow.Flow

data class LiveActivity(
    val userName: String,
    val itemType: String,
    val timestampMillis: Long
)

interface LiveTickerService {
    fun observeLiveActivity(): Flow<LiveActivity?>
    suspend fun postActivity(userName: String, itemType: String)
}
