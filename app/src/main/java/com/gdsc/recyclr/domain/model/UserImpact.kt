package com.gdsc.recyclr.domain.model

data class UserImpact(
    val userId: String,
    val totalScans: Int,
    val wasteDivertedKg: Float,
    val co2SavedKg: Float,
    val energyRecoveredKwh: Float,
    val treesEquivalent: Int,
    val pointsBalance: Int,
    /** Horodatage serveur / dernière synchro si disponible (ms). */
    val lastUpdatedMillis: Long? = null
)
