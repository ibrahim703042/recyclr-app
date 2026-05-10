package com.gdsc.recyclr.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.Response.Loading
import com.gdsc.recyclr.domain.model.Response.Success
import com.gdsc.recyclr.domain.model.UserImpact
import com.gdsc.recyclr.domain.repository.AuthRepository
import com.gdsc.recyclr.domain.repository.ImpactRepository
import com.gdsc.recyclr.domain.repository.ReloadUserResponse
import com.gdsc.recyclr.domain.repository.RevokeAccessResponse
import com.gdsc.recyclr.util.AppLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: AuthRepository,
    private val impactRepository: ImpactRepository
): ViewModel() {
    var revokeAccessResponse by mutableStateOf<RevokeAccessResponse>(Success(false))
        private set
    var reloadUserResponse by mutableStateOf<ReloadUserResponse>(Success(false))
        private set

    var impactResponse by mutableStateOf<Response<UserImpact>>(Loading)
        private set

    val currentUser get() = repo.currentUser

    /** Recharge profil Firebase + impact (ex. après connexion ou retour sur l’onglet). */
    fun refreshProfileSnapshot() {
        viewModelScope.launch {
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

    fun signOut() = repo.signOut()

    fun revokeAccess() = viewModelScope.launch {
        revokeAccessResponse = Loading
        revokeAccessResponse = repo.revokeAccess()
    }

    fun loadImpact() {
        val uid = repo.currentUser?.uid ?: "guest"
        viewModelScope.launch {
            impactResponse = Loading
            impactResponse = impactRepository.getUserImpact(uid)
        }
    }

    init {
        refreshProfileSnapshot()
    }
}
