package com.gdsc.recyclr.data.service.impl

import com.gdsc.recyclr.data.model.BarcodeLookupDto
import com.gdsc.recyclr.data.model.CommunityPostDto
import com.gdsc.recyclr.data.model.DonationCauseDto
import com.gdsc.recyclr.data.model.LeaderboardEntryDto
import com.gdsc.recyclr.data.model.WeeklyChallengeConfigDto
import com.gdsc.recyclr.data.service.EngagementService
import com.gdsc.recyclr.domain.model.engagement.PickupRequestDraft
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EngagementServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : EngagementService {

    override suspend fun getBarcode(barcode: String): Result<BarcodeLookupDto?> {
        return try {
            val snap = firestore.collection(COL_BARCODES).document(barcode).get().await()
            if (!snap.exists()) return Result.success(null)
            Result.success(snap.toObject(BarcodeLookupDto::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getWeeklyChallengeConfig(): Result<WeeklyChallengeConfigDto?> {
        return try {
            val snap = firestore.document(DOC_WEEKLY_CHALLENGE).get().await()
            if (!snap.exists()) return Result.success(null)
            Result.success(snap.toObject(WeeklyChallengeConfigDto::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLeaderboard(limit: Long): Result<List<LeaderboardEntryDto>> {
        return try {
            val snapshot = firestore.collection(COL_LEADERBOARD)
                .orderBy(FIELD_RANK, Query.Direction.ASCENDING)
                .limit(limit)
                .get()
                .await()
            Result.success(
                snapshot.documents.mapNotNull { it.toObject(LeaderboardEntryDto::class.java) },
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCommunityPosts(limit: Long): Result<List<CommunityPostDto>> {
        return try {
            val snapshot = firestore.collection(COL_COMMUNITY)
                .orderBy(FIELD_CREATED_AT_MILLIS, Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .await()
            Result.success(
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(CommunityPostDto::class.java)?.copy(id = doc.id)
                },
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDonationCauses(): Result<List<DonationCauseDto>> {
        return try {
            val snapshot = firestore.collection(COL_DONATIONS).get().await()
            Result.success(
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(DonationCauseDto::class.java)?.copy(id = doc.id)
                },
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun submitPickupRequest(userId: String, draft: PickupRequestDraft): Result<String> {
        return try {
            val payload = buildMap<String, Any> {
                put("userId", userId)
                put("address", draft.address)
                put("itemTypes", draft.itemTypes)
                put("estimatedKg", draft.estimatedKg)
                draft.repeatEveryWeeks?.let { put("repeatEveryWeeks", it) }
                put("createdAt", FieldValue.serverTimestamp())
            }
            val ref = firestore.collection(COL_PICKUPS).add(payload).await()
            Result.success(ref.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        const val COL_BARCODES = "barcodes"
        const val DOC_WEEKLY_CHALLENGE = "engagement_config/weekly_challenge"
        const val COL_LEADERBOARD = "leaderboard"
        const val COL_COMMUNITY = "community_posts"
        const val COL_DONATIONS = "donation_causes"
        const val COL_PICKUPS = "pickup_requests"
        const val FIELD_RANK = "rank"
        const val FIELD_CREATED_AT_MILLIS = "createdAtMillis"
    }
}
