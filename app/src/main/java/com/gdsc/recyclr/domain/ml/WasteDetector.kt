package com.gdsc.recyclr.domain.ml

import android.graphics.Bitmap

/**
 * Abstraction for on-device waste classification.
 * Implementations can use ML Kit, TensorFlow Lite, or simple heuristics.
 */
interface WasteDetector {
    suspend fun detectItemTypeFromBitmap(bitmap: Bitmap): Result<String>
}
