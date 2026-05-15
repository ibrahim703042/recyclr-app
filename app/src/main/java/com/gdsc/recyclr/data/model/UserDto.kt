package com.gdsc.recyclr.data.model

import com.gdsc.recyclr.domain.model.User
import com.gdsc.recyclr.domain.model.UserRole

/**
 * Transfert utilisateur (Firebase Auth).
 */
data class UserDto(
    val uid: String = "",
    val email: String? = null,
    val displayName: String? = null,
    val phoneNumber: String? = null,
    val photoUrl: String? = null,
    val isEmailVerified: Boolean = false,
    val authProvidersSummary: String? = null,
    val role: String = "USER"
) {
    fun toDomain(): User {
        return User(
            uid = uid,
            email = email,
            displayName = displayName,
            phoneNumber = phoneNumber,
            photoUrl = photoUrl,
            isEmailVerified = isEmailVerified,
            authProvidersSummary = authProvidersSummary,
            role = try { UserRole.valueOf(role) } catch (e: Exception) { UserRole.USER }
        )
    }

    companion object {
        fun fromDomain(user: User): UserDto {
            return UserDto(
                uid = user.uid,
                email = user.email,
                displayName = user.displayName,
                phoneNumber = user.phoneNumber,
                photoUrl = user.photoUrl,
                isEmailVerified = user.isEmailVerified,
                authProvidersSummary = user.authProvidersSummary,
                role = user.role.name
            )
        }
    }
}
