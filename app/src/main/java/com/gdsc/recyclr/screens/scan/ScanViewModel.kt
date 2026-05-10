package com.gdsc.recyclr.screens.scan

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.Response.Loading
import com.gdsc.recyclr.domain.model.ScanRecord
import com.gdsc.recyclr.domain.ml.WasteDetector
import com.gdsc.recyclr.domain.repository.AuthRepository
import com.gdsc.recyclr.domain.repository.ImpactRepository
import com.gdsc.recyclr.domain.repository.ScanRecordsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

data class ScanResult(
    val itemType: String,
    val points: Int,
    val co2SavedGrams: Float,
    val destination: String,
    val isHazardous: Boolean
)

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val scanRecordsRepository: ScanRecordsRepository,
    private val impactRepository: ImpactRepository,
    private val wasteDetector: WasteDetector
) : ViewModel() {

    var submitResponse: Response<ScanResult> by mutableStateOf(Response.Success(null))
        private set

    fun acknowledgeSubmitSuccess() {
        submitResponse = Response.Success(null)
    }

    fun acknowledgeSubmitFailure() {
        submitResponse = Response.Success(null)
    }

    fun submitManualScan(itemType: String) {
        viewModelScope.launch {
            persistScan(itemType, scanSource = "manual")
        }
    }

    fun submitFromCapturedBitmap(bitmap: Bitmap) {
        viewModelScope.launch {
            submitResponse = Loading
            val detected = withContext(Dispatchers.Default) {
                wasteDetector.detectItemTypeFromBitmap(bitmap)
            }
            val itemType = detected.getOrElse { e ->
                submitResponse = Response.Failure(
                    (e as? Exception) ?: Exception(e.message ?: e.toString())
                )
                if (!bitmap.isRecycled) bitmap.recycle()
                return@launch
            }
            if (!bitmap.isRecycled) bitmap.recycle()
            persistScan(itemType, scanSource = "ml", detectionConfidence = null)
        }
    }

    private suspend fun persistScan(
        itemType: String,
        scanSource: String,
        detectionConfidence: Float? = null
    ) {
        submitResponse = Loading

        val uid = authRepository.currentUser?.uid ?: "guest"
        val computed = computeImpact(itemType)
        val record = ScanRecord(
            id = UUID.randomUUID().toString(),
            userId = uid,
            itemType = itemType,
            pointsEarned = computed.points,
            co2SavedGrams = computed.co2SavedGrams,
            destination = computed.destination,
            timestampMillis = System.currentTimeMillis(),
            scanSource = scanSource,
            detectionConfidence = detectionConfidence,
            notes = null
        )

        val save = scanRecordsRepository.saveScanRecord(record)
        if (save is Response.Failure) {
            submitResponse = save
            return
        }

        impactRepository.updateImpactAfterScan(
            userId = uid,
            pointsDelta = computed.points,
            co2SavedGramsDelta = computed.co2SavedGrams,
            wasteDivertedKgDelta = computed.wasteDivertedKg,
            energyRecoveredKwhDelta = computed.energyRecoveredKwh,
            treesEquivalentDelta = computed.treesEquivalentDelta
        )

        submitResponse = Response.Success(computed.toResult())
    }

    private data class ImpactComputation(
        val itemType: String,
        val points: Int,
        val co2SavedGrams: Float,
        val destination: String,
        val isHazardous: Boolean,
        val wasteDivertedKg: Float,
        val energyRecoveredKwh: Float,
        val treesEquivalentDelta: Int
    ) {
        fun toResult() = ScanResult(itemType, points, co2SavedGrams, destination, isHazardous)
    }

    private fun computeImpact(itemType: String): ImpactComputation {
        return when (itemType.lowercase()) {
            "hazardous", "battery", "e-waste", "ewaste" -> ImpactComputation(
                itemType = itemType,
                points = 15,
                co2SavedGrams = 50f,
                destination = "Disposal",
                isHazardous = true,
                wasteDivertedKg = 0.1f,
                energyRecoveredKwh = 0f,
                treesEquivalentDelta = 0
            )
            "plastic" -> ImpactComputation(
                itemType = itemType,
                points = 10,
                co2SavedGrams = 120f,
                destination = "Recycling",
                isHazardous = false,
                wasteDivertedKg = 0.25f,
                energyRecoveredKwh = 0f,
                treesEquivalentDelta = 0
            )
            "paper" -> ImpactComputation(
                itemType = itemType,
                points = 8,
                co2SavedGrams = 80f,
                destination = "Recycling",
                isHazardous = false,
                wasteDivertedKg = 0.2f,
                energyRecoveredKwh = 0f,
                treesEquivalentDelta = 0
            )
            "furniture" -> ImpactComputation(
                itemType = itemType,
                points = 25,
                co2SavedGrams = 500f,
                destination = "Reuse",
                isHazardous = false,
                wasteDivertedKg = 2.0f,
                energyRecoveredKwh = 0f,
                treesEquivalentDelta = 0
            )
            else -> ImpactComputation(
                itemType = itemType,
                points = 5,
                co2SavedGrams = 60f,
                destination = "Recycling",
                isHazardous = false,
                wasteDivertedKg = 0.15f,
                energyRecoveredKwh = 0f,
                treesEquivalentDelta = 0
            )
        }
    }
}
