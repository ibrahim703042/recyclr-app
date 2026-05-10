package com.gdsc.recyclr.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop_items")
data class CachedShopItemEntity(
    @PrimaryKey val id: String,
    val title: String,
    val price: Int,
    val description: String,
    val category: String,
    val imageUrl: String
)

