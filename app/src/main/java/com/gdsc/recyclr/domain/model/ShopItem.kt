package com.gdsc.recyclr.domain.model



data class ShopItem(

    val id: String,

    val title: String,

    val price: Int,

    val description: String,

    val category: String,

    val imageUrl: String,

    val isActive: Boolean = true,

    val stockQuantity: Int? = null,

    val termsSummary: String? = null

)

