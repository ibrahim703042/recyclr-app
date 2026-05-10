package com.gdsc.recyclr.data.service.impl

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
    private val firestore: FirebaseFirestore
) : ShopService {
    
    override suspend fun getAllShopItems(): Result<List<ShopItemDto>> {
        return try {
            val snapshot = firestore.collection("shop_items").get().await()
            val items = snapshot.documents.mapNotNull { doc ->
                doc.toObject(ShopItemDto::class.java)?.copy(
                    id = doc.id.ifBlank { doc.toObject(ShopItemDto::class.java)?.id ?: "" }
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

    private fun seedItems(): List<ShopItemDto> = listOf(
        ShopItemDto(
            id = "seed_tree",
            imageUrl = "",
            title = "Plant 1 Tree",
            price = 100,
            description = "Partner plants one native tree",
            category = "nature"
        ),
        ShopItemDto(
            id = "seed_bag",
            imageUrl = "",
            title = "Reusable Shopping Bag",
            price = 250,
            description = "Eco-friendly bag",
            category = "bags"
        ),
        ShopItemDto(
            id = "seed_book",
            imageUrl = "",
            title = "Children's Recycling Book",
            price = 500,
            description = "Education material",
            category = "education"
        ),
        ShopItemDto(
            id = "seed_compost",
            imageUrl = "",
            title = "Home Compost Bin",
            price = 1_000,
            description = "Small compost solution",
            category = "home"
        )
    )
}
