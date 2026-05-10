package com.gdsc.recyclr.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.Response.Loading
import com.gdsc.recyclr.domain.model.UserImpact
import com.gdsc.recyclr.domain.repository.AuthRepository
import com.gdsc.recyclr.domain.repository.ImpactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val impactRepository: ImpactRepository
) : ViewModel() {

    val currentUser get() = authRepository.currentUser

    var impactResponse: Response<UserImpact> = Loading
        private set

    init {
        refresh()
    }

    fun refresh() {
        val uid = authRepository.currentUser?.uid ?: "guest"
        viewModelScope.launch {
            impactResponse = Loading
            impactResponse = impactRepository.getUserImpact(uid)
        }
    }
}

