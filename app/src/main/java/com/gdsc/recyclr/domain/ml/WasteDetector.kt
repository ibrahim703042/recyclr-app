package com.gdsc.recyclr.domain.ml

import android.graphics.Bitmap

/**
 * Abstraction pour la classification des déchets sur l’appareil (TensorFlow Lite, heuristiques, etc.).
 */
interface WasteDetector {
    suspend fun detectItemTypeFromBitmap(bitmap: Bitmap): Result<WasteDetection>

    /** Télécharge ou met à jour le modèle hébergé Firebase ML (no-op si non implémenté ou nom vide). */
    suspend fun syncHostedModelIfAvailable() {}
}
