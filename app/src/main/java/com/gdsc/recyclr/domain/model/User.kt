package com.gdsc.recyclr.domain.model

data class User(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val phoneNumber: String?,
    val photoUrl: String?,
    val isEmailVerified: Boolean,
    /** Résumé lisible des fournisseurs (google.com, phone, password, …). */
    val authProvidersSummary: String?
)
