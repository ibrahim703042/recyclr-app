package com.gdsc.recyclr.domain.model

data class CollectorLocation(
    val id: String,
    val name: String,
    val lat: Double,
    val lng: Double,
    val vehicleType: String
)
