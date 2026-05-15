package com.gdsc.recyclr.data.repository

import com.gdsc.recyclr.data.local.dao.UserImpactDao
import com.gdsc.recyclr.data.local.entities.CachedUserImpactEntity
import com.gdsc.recyclr.data.model.UserImpactDto
import com.gdsc.recyclr.data.service.ImpactService
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.UserImpact
import com.gdsc.recyclr.domain.model.UserRole
import com.gdsc.recyclr.domain.repository.AuthRepository
import com.gdsc.recyclr.domain.repository.ImpactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImpactRepositoryImpl @Inject constructor(
    private val service: ImpactService,
    private val dao: UserImpactDao,
    private val authRepository: AuthRepository,
) : ImpactRepository {

    override fun observeUserImpact(userId: String): Flow<Response<UserImpact>> {
        return dao.observe(userId)
            .map { entity ->
                if (entity != null) Response.Success(entity.toDomain())
                else Response.Loading
            }
            .onStart {
                // Trigger a refresh from network when we start observing
                runCatching { getUserImpact(userId) }
            }
    }

    override suspend fun getUserImpact(userId: String): Response<UserImpact> {
        if (userId == "guest") {
            val cached = dao.get(userId)?.toDomain() ?: defaultImpact(userId).toDomain()
            return Response.Success(cached)
        }

        return service.getUserImpact(userId)
            .fold(
                onSuccess = { dto ->
                    val impact = (dto ?: defaultImpact(userId)).toDomain()
                    dao.upsert(impact.toCached())
                    Response.Success(impact)
                },
                onFailure = {
                    val cached = dao.get(userId)?.toDomain()
                    if (cached != null) Response.Success(cached) else Response.Failure(it as Exception)
                }
            )
    }

    override suspend fun updateImpactAfterScan(
        userId: String,
        pointsDelta: Int,
        co2SavedGramsDelta: Float,
        wasteDivertedKgDelta: Float,
        energyRecoveredKwhDelta: Float,
        treesEquivalentDelta: Int
    ): Response<Boolean> {
        val user = if (userId != "guest") authRepository.currentUser else null
        val displayName = user?.displayName ?: "Green Hero"
        val role = user?.role ?: UserRole.USER

        if (userId == "guest") {
            val base = dao.get(userId)?.toDomain() ?: defaultImpact(userId).toDomain()
            val updated = base.copy(
                displayName = "Guest",
                role = UserRole.USER,
                totalScans = base.totalScans + 1,
                wasteDivertedKg = base.wasteDivertedKg + wasteDivertedKgDelta,
                co2SavedKg = base.co2SavedKg + (co2SavedGramsDelta / 1000f),
                energyRecoveredKwh = base.energyRecoveredKwh + energyRecoveredKwhDelta,
                treesEquivalent = base.treesEquivalent + treesEquivalentDelta,
                pointsBalance = base.pointsBalance + pointsDelta
            )
            dao.upsert(updated.toCached())
            return Response.Success(true)
        }

        return service.getUserImpact(userId)
            .fold(
                onSuccess = { current ->
                    val base = (current ?: defaultImpact(userId))
                    val updated = base.copy(
                        displayName = displayName,
                        role = role.name,
                        totalScans = base.totalScans + 1,
                        wasteDivertedKg = base.wasteDivertedKg + wasteDivertedKgDelta,
                        co2SavedKg = base.co2SavedKg + (co2SavedGramsDelta / 1000f),
                        energyRecoveredKwh = base.energyRecoveredKwh + energyRecoveredKwhDelta,
                        treesEquivalent = base.treesEquivalent + treesEquivalentDelta,
                        pointsBalance = base.pointsBalance + pointsDelta
                    )
                    service.upsertUserImpact(updated)
                        .fold(
                            onSuccess = {
                                dao.upsert(updated.toDomain().toCached())
                                Response.Success(true)
                            },
                            onFailure = { Response.Failure(it as Exception) }
                        )
                },
                onFailure = { Response.Failure(it as Exception) }
            )
    }

    override suspend fun applyLocalPointsDelta(userId: String, pointsDelta: Int): Response<Boolean> {
        if (userId != "guest") return Response.Success(true)
        val base = dao.get(userId)?.toDomain() ?: defaultImpact(userId).toDomain()
        val updated = base.copy(pointsBalance = (base.pointsBalance + pointsDelta).coerceAtLeast(0))
        dao.upsert(updated.toCached())
        return Response.Success(true)
    }

    private fun defaultImpact(userId: String): UserImpactDto = UserImpactDto(userId = userId)

    private fun UserImpact.toCached() = CachedUserImpactEntity(
        userId = userId,
        displayName = displayName,
        role = role.name,
        totalScans = totalScans,
        wasteDivertedKg = wasteDivertedKg,
        co2SavedKg = co2SavedKg,
        energyRecoveredKwh = energyRecoveredKwh,
        treesEquivalent = treesEquivalent,
        pointsBalance = pointsBalance
    )

    private fun CachedUserImpactEntity.toDomain() = UserImpact(
        userId = userId,
        displayName = displayName,
        role = try { UserRole.valueOf(role) } catch (_: Exception) { UserRole.USER },
        totalScans = totalScans,
        wasteDivertedKg = wasteDivertedKg,
        co2SavedKg = co2SavedKg,
        energyRecoveredKwh = energyRecoveredKwh,
        treesEquivalent = treesEquivalent,
        pointsBalance = pointsBalance,
        lastUpdatedMillis = null
    )
}
