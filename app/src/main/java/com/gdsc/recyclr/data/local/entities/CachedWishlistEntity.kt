package com.gdsc.recyclr.data.local.entities

import androidx.room.Entity

@Entity(
    tableName = "wishlist_items",
    primaryKeys = ["userId", "productId"],
)
data class CachedWishlistEntity(
    val userId: String,
    val productId: String,
    val addedAtMillis: Long,
)
