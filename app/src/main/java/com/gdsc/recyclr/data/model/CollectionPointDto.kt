package com.gdsc.recyclr.data.model

import com.gdsc.recyclr.domain.model.CollectionPoint

data class CollectionPointDto(
    val id: String = "",
    val name: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val types: List<String> = emptyList(),
    val hours: String = "",
    val phone: String? = null,
    val address: String = "",
    val websiteUrl: String? = null,
    val accessibilityNote: String? = null
) {
    fun toDomain(): CollectionPoint = CollectionPoint(
        id = id,
        name = name,
        lat = lat,
        lng = lng,
        types = types,
        hours = hours,
        phone = phone,
        address = address,
        websiteUrl = websiteUrl,
        accessibilityNote = accessibilityNote
    )

    companion object {
        fun fromDomain(model: CollectionPoint): CollectionPointDto = CollectionPointDto(
            id = model.id,
            name = model.name,
            lat = model.lat,
            lng = model.lng,
            types = model.types,
            hours = model.hours,
            phone = model.phone,
            address = model.address,
            websiteUrl = model.websiteUrl,
            accessibilityNote = model.accessibilityNote
        )
    }
}
