package com.gdsc.recyclr.data.service.impl

import android.graphics.Bitmap
import com.gdsc.recyclr.data.model.ScanRecordDto
import com.gdsc.recyclr.data.service.ScanRecordsService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScanRecordsServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ScanRecordsService {
    override suspend fun saveScanRecord(record: ScanRecordDto): Result<Boolean> {
        return try {
            val docId = record.id.ifBlank { firestore.collection("scans").document().id }
            firestore.collection("scans").document(docId).set(record.copy(id = docId)).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRecentScans(userId: String, limit: Long): Result<List<ScanRecordDto>> {
        return try {
            val snapshot = firestore.collection("scans")
                .whereEqualTo("userId", userId)
                .orderBy("timestampMillis")
                .limit(limit)
                .get()
                .await()

            val items = snapshot.documents.mapNotNull { doc ->
                doc.toObject(ScanRecordDto::class.java)?.copy(id = doc.id)
            }
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadScanImage(userId: String, scanId: String, bitmap: Bitmap): Result<String> {
        return try {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
            val data = baos.toByteArray()

            val ref = storage.reference.child("scans/$scanId/${userId}_photo.jpg")
            ref.putBytes(data).await()
            val url = ref.downloadUrl.await().toString()
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

