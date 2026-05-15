package com.gdsc.recyclr.data.model

import com.gdsc.recyclr.domain.model.UserImpact
import com.gdsc.recyclr.domain.model.UserRole

data class UserImpactDto(
    val userId: String = "",
    val displayName: String = "",
    val role: String = "USER",
    val totalScans: Int = 0,
    val wasteDivertedKg: Float = 0f,
    val co2SavedKg: Float = 0f,
    val energyRecoveredKwh: Float = 0f,
    val treesEquivalent: Int = 0,
    val pointsBalance: Int = 0,
    val lastUpdatedMillis: Long? = null
) {
    fun toDomain(): UserImpact = UserImpact(
        userId = userId,
        displayName = displayName,
        role = try { UserRole.valueOf(role) } catch (e: Exception) { UserRole.USER },
        totalScans = totalScans,
        wasteDivertedKg = wasteDivertedKg,
        co2SavedKg = co2SavedKg,
        energyRecoveredKwh = energyRecoveredKwh,
        treesEquivalent = treesEquivalent,
        pointsBalance = pointsBalance,
        lastUpdatedMillis = lastUpdatedMillis
    )

    companion object {
        fun fromDomain(model: UserImpact): UserImpactDto = UserImpactDto(
            userId = model.userId,
            displayName = model.displayName,
            role = model.role.name,
            totalScans = model.totalScans,
            wasteDivertedKg = model.wasteDivertedKg,
            co2SavedKg = model.co2SavedKg,
            energyRecoveredKwh = model.energyRecoveredKwh,
            treesEquivalent = model.treesEquivalent,
            pointsBalance = model.pointsBalance,
            lastUpdatedMillis = model.lastUpdatedMillis
        )
    }
}
