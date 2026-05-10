package com.gdsc.recyclr.data.service.impl

import com.gdsc.recyclr.data.model.CollectionPointDto
import com.gdsc.recyclr.data.service.CollectionPointsService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollectionPointsServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CollectionPointsService {
    override suspend fun getAllCollectionPoints(): Result<List<CollectionPointDto>> {
        return try {
            val snapshot = firestore.collection("collection_points").get().await()
            val points = snapshot.documents.mapNotNull { doc ->
                doc.toObject(CollectionPointDto::class.java)?.copy(id = doc.id)
            }
            Result.success(points)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

