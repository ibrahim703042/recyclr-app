package com.gdsc.recyclr.data.service.impl

import com.gdsc.recyclr.data.model.CollectionPointDto
import com.gdsc.recyclr.data.model.CollectorLocationDto
import com.gdsc.recyclr.data.service.CollectionPointsService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollectionPointsServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val db: FirebaseDatabase
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
        val ref = db.getReference("live_collectors")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val collectors = snapshot.children.mapNotNull { child ->
                    val map = child.value as? Map<*, *> ?: return@mapNotNull null
                    CollectorLocationDto(
                        collectorId = child.key ?: "",
                        name = map["name"] as? String ?: "Collector",
                        lat = (map["lat"] as? Number)?.toDouble() ?: 0.0,
                        lng = (map["lng"] as? Number)?.toDouble() ?: 0.0,
                        isAvailable = map["isAvailable"] as? Boolean ?: true,
                        vehicleType = map["vehicleType"] as? String ?: "Truck"
                    )
                }
                trySend(collectors.filter { it.isAvailable })
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override suspend fun updateCollectorLocation(collectorId: String, lat: Double, lng: Double): Result<Boolean> {
        return try {
            val ref = db.getReference("live_collectors").child(collectorId)
            ref.child("lat").setValue(lat).await()
            ref.child("lng").setValue(lng).await()
            ref.child("lastUpdatedMillis").setValue(System.currentTimeMillis()).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

