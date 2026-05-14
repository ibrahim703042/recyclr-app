package com.gdsc.recyclr.data.service.impl

import com.gdsc.recyclr.data.model.ProductReviewDto
import com.gdsc.recyclr.data.model.ShopItemDto
import com.gdsc.recyclr.data.service.ShopService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase implementation of ShopService
 * Uses Firestore for rewards catalog, with a safe fallback to seed data.
 */
@Singleton
class ShopServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : ShopService {

    override suspend fun getAllShopItems(): Result<List<ShopItemDto>> {
        return try {
            val snapshot = firestore.collection("shop_items").get().await()
            val items = snapshot.documents.mapNotNull { doc ->
                doc.toObject(ShopItemDto::class.java)?.copy(
                    id = doc.id.ifBlank { doc.toObject(ShopItemDto::class.java)?.id ?: "" },
                )
            }

            if (items.isNotEmpty()) {
                Result.success(items)
            } else {
                Result.success(seedItems())
            }
        } catch (e: Exception) {
            Result.success(seedItems())
        }
    }

    override suspend fun getShopItemById(id: String): Result<ShopItemDto?> {
        return try {
            val doc = firestore.collection("shop_items").document(id).get().await()
            Result.success(doc.toObject(ShopItemDto::class.java)?.copy(id = doc.id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addShopItem(item: ShopItemDto): Result<Boolean> {
        return try {
            val docId = item.id.ifBlank { firestore.collection("shop_items").document().id }
            firestore.collection("shop_items").document(docId).set(item.copy(id = docId)).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateShopItem(item: ShopItemDto): Result<Boolean> {
        return try {
            if (item.id.isBlank()) return Result.failure(IllegalArgumentException("Missing item.id"))
            firestore.collection("shop_items").document(item.id).set(item).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteShopItem(id: String): Result<Boolean> {
        return try {
            firestore.collection("shop_items").document(id).delete().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun seedItems(): List<ShopItemDto> {
        val now = System.currentTimeMillis()
        return listOf(
            ShopItemDto(
                id = "seed_tree",
                imageUrl = "",
                images = emptyList(),
                title = "Plant 1 Tree",
                price = 100,
                marketPrice = 200,
                description = "Partner plants one native tree in your name. You’ll receive a geotagged photo and a personalised certificate. Species: Moringa or Acacia.",
                category = "nature",
                stockQuantity = 50,
                avgRating = 4.5,
                reviewsCount = 124,
                verifiedPartner = true,
                shipsFrom = "Local tree nursery, Bujumbura",
                deliverySummary = "Digital certificate + photo proof within 14 days",
                carbonOffsetTonnes = 0.05,
                locationLabel = "Lake Tanganyika basin",
                plantingSeason = "Year-round",
                giftOption = "Add a message",
                impactMetric = "Plants 1 tree",
                reviewPreviews = listOf(
                    ProductReviewDto(
                        id = "r1",
                        authorName = "Aline N.",
                        rating = 5.0,
                        comment = "Great initiative – received photo of my tree within a week.",
                        createdAtMillis = now - 2 * 24 * 60 * 60 * 1000L,
                    ),
                ),
            ),
            ShopItemDto(
                id = "seed_bag",
                imageUrl = "",
                title = "Reusable Shopping Bag",
                price = 250,
                marketPrice = 400,
                description = "Eco-friendly bag made from recycled materials.",
                category = "bags",
                stockQuantity = 120,
                avgRating = 4.2,
                reviewsCount = 58,
                verifiedPartner = true,
                shipsFrom = "Recyclr Hub, Bujumbura",
                deliverySummary = "Pickup within 5 business days",
                carbonOffsetTonnes = 0.01,
                locationLabel = "Burundi",
                plantingSeason = "—",
                giftOption = "Gift wrap available",
                impactMetric = "Reduces single-use plastic",
            ),
            ShopItemDto(
                id = "seed_book",
                imageUrl = "",
                title = "Children's Recycling Book",
                price = 500,
                description = "Education material for schools and families.",
                category = "education",
                stockQuantity = 30,
                avgRating = 4.8,
                reviewsCount = 12,
                verifiedPartner = true,
                shipsFrom = "Print partner, Gitega",
                deliverySummary = "Ships within 10 days",
                carbonOffsetTonnes = 0.0,
                locationLabel = "Burundi",
                plantingSeason = "—",
                giftOption = "Dedication page",
                impactMetric = "Supports eco literacy",
            ),
            ShopItemDto(
                id = "seed_compost",
                imageUrl = "",
                title = "Home Compost Bin",
                price = 1_000,
                marketPrice = 1_400,
                description = "Small compost solution for urban homes.",
                category = "home",
                stockQuantity = 3,
                avgRating = 4.0,
                reviewsCount = 41,
                verifiedPartner = true,
                shipsFrom = "Partner warehouse, Bujumbura",
                deliverySummary = "Delivery + setup in 14 days",
                carbonOffsetTonnes = 0.12,
                locationLabel = "Bujumbura",
                plantingSeason = "—",
                giftOption = "—",
                impactMetric = "Diverts organic waste from landfill",
            ),
        )
    }
}
