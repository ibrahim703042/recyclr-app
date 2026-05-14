package com.gdsc.recyclr.domain.model

/** Notification in-app (cache local + future sync Firestore). */
data class AppNotification(
    val id: String,
    val title: String,
    val body: String,
    val createdAtMillis: Long,
    val read: Boolean = false,
)
