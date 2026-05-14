package com.gdsc.recyclr.domain.model

/**
 * Produit boutique (équivalent README `products` / cache `shop_items`).
 */
data class ShopItem(
    val id: String,
    val title: String,
    val price: Int,
    val description: String,
    val category: String,
    val imageUrl: String,
    val isActive: Boolean = true,
    /** -1 = illimité (README). */
    val stockQuantity: Int? = null,
    val termsSummary: String? = null,
    val imageUrls: List<String> = emptyList(),
    val marketPrice: Int? = null,
    val avgRating: Float = 0f,
    val reviewsCount: Int = 0,
    val isDonation: Boolean = false,
    val verifiedPartner: Boolean = true,
    val shipsFrom: String = "",
    val deliverySummary: String = "",
    val carbonOffsetTonnes: Float = 0f,
    val locationLabel: String = "",
    val plantingSeason: String = "",
    val giftOption: String = "",
    val impactMetric: String = "",
    val reviewPreviews: List<ProductReview> = emptyList(),
) {
    val galleryUrls: List<String>
        get() = when {
            imageUrls.isNotEmpty() -> imageUrls
            imageUrl.isNotBlank() -> listOf(imageUrl)
            else -> emptyList()
        }

    val stockLabel: String
        get() = when {
            stockQuantity == null || stockQuantity < 0 -> "in_stock"
            stockQuantity == 0 -> "out_of_stock"
            stockQuantity <= 5 -> "limited"
            else -> "in_stock"
        }
}
