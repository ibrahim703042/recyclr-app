package com.gdsc.recyclr.data.local

import com.gdsc.recyclr.data.local.entities.CachedShopItemEntity
import com.gdsc.recyclr.domain.model.ProductReview
import com.gdsc.recyclr.domain.model.ShopItem
import org.json.JSONArray
import org.json.JSONObject

object ShopItemPayloadCodec {

    fun encode(item: ShopItem): String =
        JSONObject().apply {
            put("imageUrls", JSONArray(item.imageUrls))
            if (item.marketPrice != null) put("marketPrice", item.marketPrice) else put("marketPrice", JSONObject.NULL)
            put("avgRating", item.avgRating.toDouble())
            put("reviewsCount", item.reviewsCount)
            put("isDonation", item.isDonation)
            put("verifiedPartner", item.verifiedPartner)
            put("shipsFrom", item.shipsFrom)
            put("deliverySummary", item.deliverySummary)
            put("carbonOffsetTonnes", item.carbonOffsetTonnes.toDouble())
            put("locationLabel", item.locationLabel)
            put("plantingSeason", item.plantingSeason)
            put("giftOption", item.giftOption)
            put("impactMetric", item.impactMetric)
            put("isActive", item.isActive)
            if (item.stockQuantity != null) put("stockQuantity", item.stockQuantity) else put("stockQuantity", JSONObject.NULL)
            if (item.termsSummary != null) put("termsSummary", item.termsSummary) else put("termsSummary", JSONObject.NULL)
            val arr = JSONArray()
            item.reviewPreviews.forEach { r ->
                arr.put(
                    JSONObject().apply {
                        put("id", r.id)
                        put("productId", r.productId)
                        put("authorName", r.authorName)
                        put("rating", r.rating.toDouble())
                        put("comment", r.comment)
                        put("createdAtMillis", r.createdAtMillis)
                    },
                )
            }
            put("reviews", arr)
        }.toString()

    fun mergeIntoDomain(entity: CachedShopItemEntity, payload: String): ShopItem {
        val base = ShopItem(
            id = entity.id,
            title = entity.title,
            price = entity.price,
            description = entity.description,
            category = entity.category,
            imageUrl = entity.imageUrl,
        )
        if (payload.isBlank() || payload == "{}") return base
        return runCatching {
            val o = JSONObject(payload)
            val urls = mutableListOf<String>()
            o.optJSONArray("imageUrls")?.let { ja ->
                for (i in 0 until ja.length()) urls.add(ja.getString(i))
            }
            val reviews = mutableListOf<ProductReview>()
            o.optJSONArray("reviews")?.let { ja ->
                for (i in 0 until ja.length()) {
                    val r = ja.getJSONObject(i)
                    reviews.add(
                        ProductReview(
                            id = r.optString("id"),
                            productId = r.optString("productId", entity.id),
                            authorName = r.optString("authorName"),
                            rating = r.optDouble("rating", 0.0).toFloat(),
                            comment = r.optString("comment"),
                            createdAtMillis = r.optLong("createdAtMillis"),
                        ),
                    )
                }
            }
            base.copy(
                imageUrls = urls,
                marketPrice = if (o.has("marketPrice") && !o.isNull("marketPrice")) o.getInt("marketPrice") else null,
                avgRating = o.optDouble("avgRating", 0.0).toFloat(),
                reviewsCount = o.optInt("reviewsCount"),
                isDonation = o.optBoolean("isDonation"),
                verifiedPartner = o.optBoolean("verifiedPartner", true),
                shipsFrom = o.optString("shipsFrom"),
                deliverySummary = o.optString("deliverySummary"),
                carbonOffsetTonnes = o.optDouble("carbonOffsetTonnes", 0.0).toFloat(),
                locationLabel = o.optString("locationLabel"),
                plantingSeason = o.optString("plantingSeason"),
                giftOption = o.optString("giftOption"),
                impactMetric = o.optString("impactMetric"),
                isActive = o.optBoolean("isActive", true),
                stockQuantity = if (o.has("stockQuantity") && !o.isNull("stockQuantity")) o.getInt("stockQuantity") else null,
                termsSummary = if (o.has("termsSummary") && !o.isNull("termsSummary")) o.getString("termsSummary") else null,
                reviewPreviews = reviews,
            )
        }.getOrDefault(base)
    }

    fun toCached(item: ShopItem): CachedShopItemEntity =
        CachedShopItemEntity(
            id = item.id,
            title = item.title,
            price = item.price,
            description = item.description,
            category = item.category,
            imageUrl = item.imageUrl.ifBlank { item.galleryUrls.firstOrNull().orEmpty() },
            payloadJson = encode(item),
        )
}
