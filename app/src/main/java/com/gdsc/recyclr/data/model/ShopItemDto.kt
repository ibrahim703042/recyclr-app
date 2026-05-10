package com.gdsc.recyclr.data.model



import com.gdsc.recyclr.domain.model.ShopItem



data class ShopItemDto(

    val id: String = "",

    val imageUrl: String = "",

    val title: String = "",

    val price: Int = 0,

    val description: String = "",

    val category: String = "",

    val isActive: Boolean = true,

    val stockQuantity: Int? = null,

    val termsSummary: String? = null

) {

    fun toDomain(): ShopItem {

        return ShopItem(

            id = id,

            title = title,

            price = price,

            description = description,

            category = category,

            imageUrl = imageUrl,

            isActive = isActive,

            stockQuantity = stockQuantity,

            termsSummary = termsSummary

        )

    }



    companion object {

        fun fromDomain(shopItem: ShopItem): ShopItemDto {

            return ShopItemDto(

                id = shopItem.id,

                title = shopItem.title,

                price = shopItem.price,

                description = shopItem.description,

                category = shopItem.category,

                imageUrl = shopItem.imageUrl,

                isActive = shopItem.isActive,

                stockQuantity = shopItem.stockQuantity,

                termsSummary = shopItem.termsSummary

            )

        }

    }

}

