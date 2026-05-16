package com.gdsc.recyclr.data.repository

import com.gdsc.recyclr.domain.model.UserRole
import com.gdsc.recyclr.data.local.dao.BarcodeCacheDao
import com.gdsc.recyclr.data.local.dao.PickupQueueDao
import com.gdsc.recyclr.data.local.entities.CachedBarcodeEntity
import com.gdsc.recyclr.data.local.entities.LocalPickupRequestEntity
import com.gdsc.recyclr.data.model.CommunityPostDto
import com.gdsc.recyclr.data.model.DonationCauseDto
import com.gdsc.recyclr.data.model.LeaderboardEntryDto
import com.gdsc.recyclr.data.model.WeeklyChallengeConfigDto
import com.gdsc.recyclr.data.service.EngagementService
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.engagement.CommunityPost
import com.gdsc.recyclr.domain.model.engagement.DonationCause
import com.gdsc.recyclr.domain.model.engagement.EcoStreak
import com.gdsc.recyclr.domain.model.engagement.HomeDashboard
import com.gdsc.recyclr.domain.model.engagement.LeaderboardEntry
import com.gdsc.recyclr.domain.model.engagement.PickupRequestDraft
import com.gdsc.recyclr.domain.model.engagement.RecWallet
import com.gdsc.recyclr.domain.model.engagement.UserBadge
import com.gdsc.recyclr.domain.model.engagement.WeeklyChallenge
import com.gdsc.recyclr.domain.repository.EngagementRepository
import com.gdsc.recyclr.domain.repository.ScanRecordsRepository
import java.util.Calendar
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min

@Singleton
class EngagementRepositoryImpl @Inject constructor(
    private val scanRecordsRepository: ScanRecordsRepository,
    private val engagementService: EngagementService,
    private val barcodeCacheDao: BarcodeCacheDao,
    private val pickupQueueDao: PickupQueueDao,
) : EngagementRepository {

    override suspend fun getHomeDashboard(userId: String, pointsBalance: Int): Response<HomeDashboard> {
        return runCatching {
            val streak = computeStreak(userId)
            val challenge = resolveChallenge(userId)
            val leaderboard = resolveLeaderboardForUser(userId)
            val communityHighlights = resolveCommunityPosts(userId).take(8)
            Response.Success(
                HomeDashboard(
                    streak = streak,
                    challenge = challenge,
                    leaderboard = leaderboard.take(3),
                    communityHighlights = communityHighlights,
                    wallet = buildWallet(pointsBalance),
                    badges = defaultBadges(streak.currentDays, challenge.currentScans),
                ),
            )
        }.getOrElse { error ->
            Response.Failure(error as? Exception ?: Exception(error.message, error))
        }
    }

    override suspend fun getLeaderboard(userId: String): Response<List<LeaderboardEntry>> {
        return Response.Success(resolveLeaderboardForUser(userId))
    }

    override suspend fun getChallenge(userId: String): Response<WeeklyChallenge> {
        return Response.Success(resolveChallenge(userId))
    }

    override suspend fun getCommunityFeed(userId: String): Response<List<CommunityPost>> {
        return Response.Success(resolveCommunityPosts(userId))
    }

    override suspend fun getWallet(userId: String, pointsBalance: Int): Response<RecWallet> {
        return Response.Success(buildWallet(pointsBalance))
    }

    override suspend fun getDonationCauses(userId: String): Response<List<DonationCause>> {
        return Response.Success(resolveDonationCauses(userId))
    }

    override suspend fun submitPickupRequest(userId: String, draft: PickupRequestDraft): Response<Boolean> {
        if (draft.address.isBlank() || draft.itemTypes.isEmpty()) {
            return Response.Failure(IllegalArgumentException("Address and at least one item type are required."))
        }
        val localId = UUID.randomUUID().toString()
        val pending = LocalPickupRequestEntity(
            id = localId,
            userId = userId,
            address = draft.address,
            itemsCsv = draft.itemTypes.joinToString(","),
            estimatedKg = draft.estimatedKg,
            repeatEveryWeeks = draft.repeatEveryWeeks,
            createdAtMillis = System.currentTimeMillis(),
            synced = false,
        )
        if (userId == "guest") {
            pickupQueueDao.insert(pending.copy(synced = true))
            return Response.Success(true)
        }
        return engagementService.submitPickupRequest(userId, draft).fold(
            onSuccess = { Response.Success(true) },
            onFailure = {
                pickupQueueDao.insert(pending)
                Response.Success(true)
            },
        )
    }

    override suspend fun lookupBarcode(barcode: String, userId: String): Response<Pair<String, Int>?> {
        val trimmed = barcode.trim()
        if (trimmed.isEmpty()) return Response.Success(null)

        val allowRemote = userId != "guest"
        if (allowRemote) {
            engagementService.getBarcode(trimmed).fold(
                onSuccess = { dto ->
                    if (dto != null && dto.itemLabel.isNotBlank()) {
                        barcodeCacheDao.upsert(
                            CachedBarcodeEntity(
                                barcode = trimmed,
                                itemLabel = dto.itemLabel,
                                points = dto.points.toInt().coerceAtLeast(1),
                                fetchedAtMillis = System.currentTimeMillis(),
                            ),
                        )
                        return Response.Success(dto.itemLabel to dto.points.toInt())
                    }
                },
                onFailure = { /* fall through */ },
            )
        }

        barcodeCacheDao.get(trimmed)?.let {
            return Response.Success(it.itemLabel to it.points)
        }

        return Response.Success(SEED_BARCODES[trimmed])
    }

    override suspend fun createCommunityPost(post: CommunityPost): Response<String> {
        return engagementService.createCommunityPost(CommunityPostDto.fromDomain(post))
            .fold(
                onSuccess = { Response.Success(it) },
                onFailure = { Response.Failure(it as Exception) }
            )
    }

    override suspend fun deleteCommunityPost(postId: String): Response<Boolean> {
        return engagementService.deleteCommunityPost(postId)
            .fold(
                onSuccess = { Response.Success(it) },
                onFailure = { Response.Failure(it as Exception) }
            )
    }

    override suspend fun getAdminStats(): Response<Map<String, Long>> {
        return engagementService.getAdminStats()
            .fold(
                onSuccess = { Response.Success(it) },
                onFailure = { Response.Failure(it as Exception) }
            )
    }

    private suspend fun resolveChallenge(userId: String): WeeklyChallenge {
        val config = if (userId != "guest") {
            engagementService.getWeeklyChallengeConfig().getOrNull()
        } else {
            null
        } ?: defaultChallengeConfig()
        val target = config.targetScans.toInt().coerceAtLeast(1)
        val current = min(config.communityScansCount.toInt(), target)
        return WeeklyChallenge(
            id = config.id.ifBlank { "lake-week" },
            title = config.title.ifBlank { "Clean Lake Tanganyika Week" },
            description = config.description.ifBlank {
                "Reach the community scan goal to unlock a tree-planting event."
            },
            targetScans = target,
            currentScans = current,
            rewardPoints = config.rewardPoints.toInt(),
            endsInDays = config.endsInDays.toInt(),
        )
    }

    private suspend fun resolveLeaderboardForUser(userId: String): List<LeaderboardEntry> {
        if (userId == "guest") return seedLeaderboard()
        return engagementService.getLeaderboard(20).fold(
            onSuccess = { list ->
                list.map { it.toDomain().copy(isCurrentUser = it.userId == userId) }
            },
            onFailure = { emptyList() },
        )
    }

    private fun LeaderboardEntryDto.toDomain() = LeaderboardEntry(
        userId = userId,
        rank = rank.toInt(),
        name = displayName,
        points = points.toInt(),
        isCurrentUser = isCurrentUser,
        role = try { UserRole.valueOf(role) } catch (_: Exception) { UserRole.USER }
    )

    private suspend fun resolveCommunityPosts(userId: String): List<CommunityPost> {
        if (userId == "guest") return seedCommunity()
        return engagementService.getCommunityPosts(30).fold(
            onSuccess = { list -> list.map { it.toDomain() } },
            onFailure = { emptyList() },
        )
    }

    private fun CommunityPostDto.toDomain() = CommunityPost(
        id = id.ifBlank { UUID.randomUUID().toString() },
        author = author,
        groupName = groupName,
        message = message,
        likes = likes.toInt(),
        isReport = isReport,
        createdAtMillis = createdAtMillis.takeIf { it > 0L },
    )

    private suspend fun resolveDonationCauses(userId: String): List<DonationCause> {
        if (userId == "guest") return seedDonations()
        return engagementService.getDonationCauses().fold(
            onSuccess = { list ->
                if (list.isEmpty()) seedDonations()
                else list.map { it.toDomain() }
            },
            onFailure = { seedDonations() },
        )
    }

    private fun DonationCauseDto.toDomain() = DonationCause(
        id = id.ifBlank { title.hashCode().toString() },
        title = title,
        description = description,
        pointsCost = pointsCost.toInt(),
        category = category,
    )

    private fun defaultChallengeConfig() = WeeklyChallengeConfigDto(
        communityScansCount = 18,
    )

    private suspend fun scanHistory(userId: String) = when (val history = scanRecordsRepository.getRecentScans(userId, 120)) {
        is Response.Success -> history.data.orEmpty()
        else -> emptyList()
    }

    private suspend fun computeStreak(userId: String): EcoStreak {
        val scans = scanHistory(userId)
        val uniqueDays = scans
            .map { TimeUnit.MILLISECONDS.toDays(it.timestampMillis) }
            .distinct()
            .sortedDescending()
        var current = 0
        var expectedDay = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())
        for (day in uniqueDays) {
            if (day == expectedDay || day == expectedDay - 1) {
                current++
                expectedDay = day - 1
            } else {
                break
            }
        }
        if (current == 0 && scans.isNotEmpty()) current = 1
        val multiplier = when {
            current >= 30 -> 2.0f
            current >= 7 -> 1.5f
            current >= 3 -> 1.2f
            else -> 1.0f
        }
        val bonus = when {
            current >= 30 -> 500
            current >= 7 -> 50
            current >= 1 -> 5
            else -> 0
        }
        return EcoStreak(
            currentDays = current,
            bestDays = maxOf(current, 12),
            multiplier = multiplier,
            bonusPointsToday = bonus,
        )
    }

    private fun weekStartMillis(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun seedLeaderboard() = listOf(
        LeaderboardEntry("1", 1, "Aline N.", 1280),
        LeaderboardEntry("2", 2, "Eric M.", 1140),
        LeaderboardEntry("guest", 3, "You", 1060, isCurrentUser = true),
        LeaderboardEntry("4", 4, "Divine K.", 980),
        LeaderboardEntry("5", 5, "Moise T.", 910),
    )

    private fun seedCommunity() = listOf(
        CommunityPost(
            id = "1",
            author = "Ngagara Recycling Team",
            groupName = "Ngagara Recycling Team",
            message = "Collected 42 kg of plastic along the lakeshore this morning.",
            likes = 36,
            createdAtMillis = System.currentTimeMillis() - 2L * 60L * 60L * 1000L,
        ),
        CommunityPost(
            id = "2",
            author = "Eric M.",
            groupName = "City",
            message = "Repaired a school chair instead of throwing it away.",
            likes = 18,
            createdAtMillis = System.currentTimeMillis() - 26L * 60L * 60L * 1000L,
        ),
        CommunityPost(
            id = "3",
            author = "Eco-Watch",
            groupName = "Reports",
            message = "Illegal dumping reported near Kamenge market. Moderator review pending.",
            likes = 9,
            isReport = true,
        ),
    )

    private fun seedDonations() = listOf(
        DonationCause(
            id = "cart",
            title = "Waste cart for cooperative",
            description = "Fund a shared collection cart for a neighborhood cooperative.",
            pointsCost = 250,
            category = "Infrastructure",
        ),
        DonationCause(
            id = "lesson",
            title = "Recycling lesson at school",
            description = "Sponsor an in-class recycling workshop for students.",
            pointsCost = 180,
            category = "Education",
        ),
        DonationCause(
            id = "tree",
            title = "Community tree planting",
            description = "Convert points into native trees planted near Lake Tanganyika.",
            pointsCost = 120,
            category = "Restoration",
        ),
    )

    private fun buildWallet(pointsBalance: Int): RecWallet {
        val rate = 100
        return RecWallet(
            recBalance = pointsBalance / rate.toDouble(),
            pointsBalance = pointsBalance,
            conversionRate = rate,
            carbonCreditsTonnes = (pointsBalance / 2000f).coerceAtLeast(0.05f),
            lifetimeRecMinted = (pointsBalance / rate.toDouble()) + 3.4,
        )
    }

    private fun defaultBadges(streakDays: Int, weeklyScans: Int): List<UserBadge> = listOf(
        UserBadge("streak", "Eco-Streak", "Scan on consecutive days.", earned = streakDays >= 3),
        UserBadge("lake", "Lake Guardian", "Join a lake cleanup challenge.", earned = weeklyScans >= 10),
        UserBadge("watch", "Eco-Watch", "Report illegal dumping.", earned = false),
        UserBadge("reuse", "Reuse Hero", "Complete a reuse mini-game.", earned = false),
    )

    companion object {
        private val SEED_BARCODES = mapOf(
            "6001234567890" to ("Plastic bottle" to 8),
            "6001234567891" to ("Glass jar" to 10),
            "6001234567892" to ("Paper carton" to 6),
        )
    }
}
