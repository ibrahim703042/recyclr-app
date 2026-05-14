package com.gdsc.recyclr.domain.model

/** Avis produit (aligné collection Firestore `reviews`). */
data class ProductReview(
    val id: String,
    val productId: String,
    val authorName: String,
    val rating: Float,
    val comment: String,
    val createdAtMillis: Long,
)
