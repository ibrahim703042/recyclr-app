package com.gdsc.recyclr.data.service.impl

import com.gdsc.recyclr.data.service.LiveActivity
import com.gdsc.recyclr.data.service.LiveTickerService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LiveTickerServiceImpl @Inject constructor(
    private val db: FirebaseDatabase
) : LiveTickerService {

    override fun observeLiveActivity(): Flow<LiveActivity?> = callbackFlow {
        val ref = db.getReference("live_ticker")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val map = snapshot.value as? Map<*, *> ?: return
                val activity = LiveActivity(
                    userName = map["userName"] as? String ?: "Someone",
                    itemType = map["itemType"] as? String ?: "something",
                    timestampMillis = map["timestampMillis"] as? Long ?: 0L
                )
                trySend(activity)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override suspend fun postActivity(userName: String, itemType: String) {
        try {
            val ref = db.getReference("live_ticker")
            val data = mapOf(
                "userName" to userName,
                "itemType" to itemType,
                "timestampMillis" to System.currentTimeMillis()
            )
            ref.setValue(data).await()
        } catch (e: Exception) {
            // Log error but don't fail core logic
        }
    }
}
