package com.gdsc.recyclr.data.model

data class CollectorLocationDto(
    val collectorId: String = "",
    val name: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val isAvailable: Boolean = true,
    val vehicleType: String = "Truck",
    val lastUpdatedMillis: Long = 0L
)
