package com.gdsc.recyclr.screens.profile

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdsc.recyclr.auth.GoogleCredentialAuth
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.Response.Loading
import com.gdsc.recyclr.domain.model.Response.Success
import com.gdsc.recyclr.domain.model.User
import com.gdsc.recyclr.domain.model.UserImpact
import com.gdsc.recyclr.domain.model.engagement.UserBadge
import com.gdsc.recyclr.domain.repository.AuthRepository
import com.gdsc.recyclr.domain.repository.EngagementRepository
import com.gdsc.recyclr.domain.repository.ImpactRepository
import com.gdsc.recyclr.domain.repository.ReloadUserResponse
import com.gdsc.recyclr.domain.repository.RevokeAccessResponse
import com.gdsc.recyclr.util.AppLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: AuthRepository,
    private val impactRepository: ImpactRepository,
    private val engagementRepository: EngagementRepository,
    @ApplicationContext private val appContext: Context,
) : ViewModel() {
    var revokeAccessResponse by mutableStateOf<RevokeAccessResponse>(Success(false))
        private set
    var reloadUserResponse by mutableStateOf<ReloadUserResponse>(Success(false))
        private set

    var impactResponse by mutableStateOf<Response<UserImpact>>(Loading)
        private set

    var badges by mutableStateOf<List<UserBadge>>(emptyList())
        private set

    var userWithRole by mutableStateOf<User?>(null)
        private set

    var updateDisplayNameResponse by mutableStateOf<Response<Boolean>>(Success(false))
        private set

    val currentUser get() = userWithRole ?: repo.currentUser

    val accountActiveDays: Int
        get() {
            val createdAt = repo.getAccountCreationMillis() ?: return 1
            val elapsedDays = ((System.currentTimeMillis() - createdAt) / 86_400_000L).toInt()
            return maxOf(1, elapsedDays + 1)
        }

    fun getMemberSinceFormatted(locale: Locale = Locale.getDefault()): String? {
        val createdAt = repo.getAccountCreationMillis() ?: return null
        return DateFormat.getDateInstance(DateFormat.MEDIUM, locale).format(Date(createdAt))
    }

    fun refreshProfileSnapshot() {
        viewModelScope.launch {
            userWithRole = repo.getCurrentUser()
            runCatching {
                if (repo.currentUser != null) {
                    reloadUserResponse = Loading
                    reloadUserResponse = repo.reloadUser()
                }
            }.onFailure { AppLogger.w("refreshProfile reloadUser", it) }
            loadImpact()
        }
    }

    fun reloadUser() = viewModelScope.launch {
        reloadUserResponse = Loading
        reloadUserResponse = repo.reloadUser()
    }

    val isEmailVerified get() = repo.currentUser?.isEmailVerified ?: false

    fun signOut() {
        viewModelScope.launch {
            GoogleCredentialAuth.clearCredentialState(appContext)
            repo.signOut()
        }
    }

    fun revokeAccess() = viewModelScope.launch {
        revokeAccessResponse = Loading
        revokeAccessResponse = repo.revokeAccess()
        GoogleCredentialAuth.clearCredentialState(appContext)
    }

    fun updateDisplayName(displayName: String) = viewModelScope.launch {
        updateDisplayNameResponse = Loading
        updateDisplayNameResponse = repo.updateDisplayName(displayName)
        if (updateDisplayNameResponse is Success) {
            repo.reloadUser()
            refreshProfileSnapshot()
        }
    }

    fun loadImpact() {
        val uid = repo.currentUser?.uid ?: "guest"
        viewModelScope.launch {
            impactResponse = Loading
            impactResponse = impactRepository.getUserImpact(uid)
            val points = (impactResponse as? Success)?.data?.pointsBalance ?: 0
            when (val dashboard = engagementRepository.getHomeDashboard(uid, points)) {
                is Success -> badges = dashboard.data?.badges.orEmpty()
                else -> badges = emptyList()
            }
        }
    }

    init {
        refreshProfileSnapshot()
    }
}
