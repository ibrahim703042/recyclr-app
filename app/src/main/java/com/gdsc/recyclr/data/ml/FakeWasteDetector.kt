package com.gdsc.recyclr.data.ml

import android.graphics.Bitmap
import com.gdsc.recyclr.domain.ml.WasteDetection
import com.gdsc.recyclr.domain.ml.WasteDetector
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeWasteDetector @Inject constructor() : WasteDetector {
    override suspend fun detectItemTypeFromBitmap(bitmap: Bitmap): Result<WasteDetection> {
        if (bitmap.width < 16 || bitmap.height < 16) {
            return Result.failure(IllegalArgumentException("Image trop petite pour l'analyse"))
        }
        // Démo : heuristique simple sans TFLite (repli ou tests).
        val types = listOf("Plastic", "Paper", "Furniture")
        val idx = (bitmap.width + bitmap.height) % types.size
        return Result.success(WasteDetection(itemType = types[idx], confidence = 0.55f))
    }
}
