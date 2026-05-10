package com.gdsc.recyclr.domain.model

data class CollectionPoint(
    val id: String,
    val name: String,
    val lat: Double,
    val lng: Double,
    val types: List<String>,
    val hours: String,
    val phone: String?,
    val address: String = "",
    val websiteUrl: String? = null,
    val accessibilityNote: String? = null
)
