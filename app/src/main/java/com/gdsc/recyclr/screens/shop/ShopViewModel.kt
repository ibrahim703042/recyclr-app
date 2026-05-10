package com.gdsc.recyclr.screens.shop

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.gdsc.recyclr.domain.model.ShopItem
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.Response.Loading
import com.gdsc.recyclr.domain.model.Redemption
import com.gdsc.recyclr.domain.repository.AuthRepository
import com.gdsc.recyclr.domain.repository.RedemptionRepository
import com.gdsc.recyclr.domain.repository.ShopRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val repository: ShopRepository,
    private val authRepository: AuthRepository,
    private val redemptionRepository: RedemptionRepository
) : ViewModel() {
    
    var shopItemsResponse by mutableStateOf<Response<List<ShopItem>>>(Loading)
        private set

    var redemptionHistoryResponse by mutableStateOf<Response<List<Redemption>>>(Loading)
        private set

    var redeemResponse by mutableStateOf<Response<Redemption>>(Response.Success(null))
        private set

    fun acknowledgeRedeemFeedback() {
        redeemResponse = Response.Success(null)
    }
    
    init {
        loadShopItems()
        loadHistory()
    }
    
    private fun loadShopItems() {
        viewModelScope.launch {
            shopItemsResponse = Loading
            shopItemsResponse = repository.getAllShopItems()
        }
    }

    fun loadHistory() {
        val uid = authRepository.currentUser?.uid ?: "guest"
        viewModelScope.launch {
            redemptionHistoryResponse = Loading
            redemptionHistoryResponse = redemptionRepository.getHistory(uid)
        }
    }

    fun redeem(item: ShopItem) {
        val uid = authRepository.currentUser?.uid ?: "guest"
        viewModelScope.launch {
            redeemResponse = Loading
            redeemResponse = redemptionRepository.redeem(uid, item)
            loadHistory()
        }
    }
}
