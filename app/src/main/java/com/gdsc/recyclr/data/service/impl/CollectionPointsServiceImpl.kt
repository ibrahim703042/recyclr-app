package com.gdsc.recyclr.data.service.impl

import com.gdsc.recyclr.data.model.CollectionPointDto
import com.gdsc.recyclr.data.model.CollectorLocationDto
import com.gdsc.recyclr.data.service.CollectionPointsService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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

    override fun observeCollectors(): Flow<List<CollectorLocationDto>> = callbackFlow {
        val subscription = firestore.collection("collectors")
            .whereEqualTo("isAvailable", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val collectors = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(CollectorLocationDto::class.java)?.copy(collectorId = doc.id)
                    }
                    trySend(collectors)
                }
            }
        awaitClose { subscription.remove() }
    }
}

