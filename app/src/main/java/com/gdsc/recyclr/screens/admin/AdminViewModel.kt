package com.gdsc.recyclr.screens.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.repository.EngagementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val engagementRepository: EngagementRepository
) : ViewModel() {

    var statsResponse: Response<Map<String, Long>> by mutableStateOf(Response.Loading)
        private set

    init {
        refreshStats()
    }

    fun refreshStats() {
        viewModelScope.launch {
            statsResponse = Response.Loading
            statsResponse = engagementRepository.getAdminStats()
        }
    }
}
