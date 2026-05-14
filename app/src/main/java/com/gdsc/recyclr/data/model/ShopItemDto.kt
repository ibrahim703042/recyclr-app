package com.gdsc.recyclr.data.model

import com.gdsc.recyclr.domain.model.ProductReview
import com.gdsc.recyclr.domain.model.ShopItem

data class ProductReviewDto(
    val id: String = "",
    val authorName: String = "",
    val rating: Double = 5.0,
    val comment: String = "",
    val createdAtMillis: Long = 0,
) {
    fun toDomain(productId: String) = ProductReview(
        id = id.ifBlank { "${productId}_${authorName.hashCode()}" },
        productId = productId,
        authorName = authorName,
        rating = rating.toFloat(),
        comment = comment,
        createdAtMillis = createdAtMillis,
    )
}

data class ShopItemDto(
    val id: String = "",
    val imageUrl: String = "",
    /** Alias Firestore `images` si présent côté admin. */
    val images: List<String> = emptyList(),
    val title: String = "",
    val price: Int = 0,
    val description: String = "",
    val category: String = "",
    val isActive: Boolean = true,
    val stockQuantity: Int? = null,
    val termsSummary: String? = null,
    val marketPrice: Int? = null,
    val avgRating: Double = 0.0,
    val reviewsCount: Int = 0,
    val isDonation: Boolean = false,
    val verifiedPartner: Boolean = true,
    val shipsFrom: String = "",
    val deliverySummary: String = "",
    val carbonOffsetTonnes: Double = 0.0,
    val locationLabel: String = "",
    val plantingSeason: String = "",
    val giftOption: String = "",
    val impactMetric: String = "",
    val reviewPreviews: List<ProductReviewDto> = emptyList(),
) {
    fun toDomain(): ShopItem {
        val urls = when {
            images.isNotEmpty() -> images
            imageUrl.isNotBlank() -> listOf(imageUrl)
            else -> emptyList()
        }
        val primary = urls.firstOrNull().orEmpty()
        return ShopItem(
            id = id,
            title = title,
            price = price,
            description = description,
            category = category,
            imageUrl = primary,
            isActive = isActive,
            stockQuantity = stockQuantity,
            termsSummary = termsSummary,
            imageUrls = urls,
            marketPrice = marketPrice,
            avgRating = avgRating.toFloat(),
            reviewsCount = reviewsCount,
            isDonation = isDonation,
            verifiedPartner = verifiedPartner,
            shipsFrom = shipsFrom,
            deliverySummary = deliverySummary,
            carbonOffsetTonnes = carbonOffsetTonnes.toFloat(),
            locationLabel = locationLabel,
            plantingSeason = plantingSeason,
            giftOption = giftOption,
            impactMetric = impactMetric,
            reviewPreviews = reviewPreviews.map { it.toDomain(id) },
        )
    }

    companion object {
        fun fromDomain(shopItem: ShopItem): ShopItemDto =
            ShopItemDto(
                id = shopItem.id,
                imageUrl = shopItem.imageUrl,
                images = shopItem.imageUrls,
                title = shopItem.title,
                price = shopItem.price,
                description = shopItem.description,
                category = shopItem.category,
                isActive = shopItem.isActive,
                stockQuantity = shopItem.stockQuantity,
                termsSummary = shopItem.termsSummary,
                marketPrice = shopItem.marketPrice,
                avgRating = shopItem.avgRating.toDouble(),
                reviewsCount = shopItem.reviewsCount,
                isDonation = shopItem.isDonation,
                verifiedPartner = shopItem.verifiedPartner,
                shipsFrom = shopItem.shipsFrom,
                deliverySummary = shopItem.deliverySummary,
                carbonOffsetTonnes = shopItem.carbonOffsetTonnes.toDouble(),
                locationLabel = shopItem.locationLabel,
                plantingSeason = shopItem.plantingSeason,
                giftOption = shopItem.giftOption,
                impactMetric = shopItem.impactMetric,
                reviewPreviews = shopItem.reviewPreviews.map {
                    ProductReviewDto(
                        id = it.id,
                        authorName = it.authorName,
                        rating = it.rating.toDouble(),
                        comment = it.comment,
                        createdAtMillis = it.createdAtMillis,
                    )
                },
            )
    }
}
