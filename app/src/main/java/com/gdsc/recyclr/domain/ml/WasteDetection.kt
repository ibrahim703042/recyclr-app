package com.gdsc.recyclr.domain.ml

/**
 * Résultat d’inférence on-device (TensorFlow Lite ou repli).
 *
 * @param itemType Libellé aligné sur les types Recyclr (ex. « Plastic », « Paper »).
 * @param confidence Probabilité estimée pour la classe prédite, typiquement dans [0, 1].
 */
data class WasteDetection(
    val itemType: String,
    val confidence: Float,
)
