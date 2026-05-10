package com.gdsc.recyclr.data.service.impl

import com.gdsc.recyclr.data.model.RedemptionDto
import com.gdsc.recyclr.data.model.ShopItemDto
import com.gdsc.recyclr.data.model.UserImpactDto
import com.gdsc.recyclr.data.service.RedemptionService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RedemptionServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : RedemptionService {

    override suspend fun redeem(userId: String, item: ShopItemDto): Result<RedemptionDto> {
        return try {
            val redemptionId = firestore.collection("redemptions").document().id
            val impactRef = firestore.collection("user_impact").document(userId)
            val redemptionRef = firestore.collection("redemptions").document(redemptionId)

            val result = firestore.runTransaction { tx ->
                val impactSnap = tx.get(impactRef)
                val current = impactSnap.toObject(UserImpactDto::class.java) ?: UserImpactDto(userId = userId)
                val balance = current.pointsBalance
                if (balance < item.price) {
                    throw IllegalStateException("Not enough points")
                }

                val updated = current.copy(pointsBalance = balance - item.price)
                tx.set(impactRef, updated)

                val qrPayload = "RECYCLR:$userId:$redemptionId:${item.id}:${item.price}"
                val dto = RedemptionDto(
                    id = redemptionId,
                    userId = userId,
                    shopItemId = item.id,
                    shopItemTitle = item.title,
                    pointsCost = item.price,
                    timestampMillis = System.currentTimeMillis(),
                    qrPayload = qrPayload
                )
                tx.set(redemptionRef, dto)
                dto
            }.await()

            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getHistory(userId: String, limit: Long): Result<List<RedemptionDto>> {
        return try {
            val snapshot = firestore.collection("redemptions")
                .whereEqualTo("userId", userId)
                .orderBy("timestampMillis")
                .limit(limit)
                .get()
                .await()

            val items = snapshot.documents.mapNotNull { doc ->
                doc.toObject(RedemptionDto::class.java)?.copy(id = doc.id)
            }
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

