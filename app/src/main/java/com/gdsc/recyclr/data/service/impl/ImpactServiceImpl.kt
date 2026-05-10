package com.gdsc.recyclr.data.service.impl

import com.gdsc.recyclr.data.model.UserImpactDto
import com.gdsc.recyclr.data.service.ImpactService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImpactServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ImpactService {
    override suspend fun getUserImpact(userId: String): Result<UserImpactDto?> {
        return try {
            val doc = firestore.collection("user_impact").document(userId).get().await()
            Result.success(doc.toObject(UserImpactDto::class.java)?.copy(userId = userId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun upsertUserImpact(impact: UserImpactDto): Result<Boolean> {
        return try {
            firestore.collection("user_impact").document(impact.userId).set(impact).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

