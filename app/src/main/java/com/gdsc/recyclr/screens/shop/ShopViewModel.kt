package com.gdsc.recyclr.screens.shop

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdsc.recyclr.data.local.dao.ChatMessageDao
import com.gdsc.recyclr.data.local.dao.WishlistDao
import com.gdsc.recyclr.data.local.entities.CachedWishlistEntity
import com.gdsc.recyclr.data.local.preferences.BadgePreferencesStore
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.Response.Loading
import com.gdsc.recyclr.domain.model.Response.Success
import com.gdsc.recyclr.domain.model.Redemption
import com.gdsc.recyclr.domain.model.ShopItem
import com.gdsc.recyclr.domain.repository.AuthRepository
import com.gdsc.recyclr.domain.repository.ImpactRepository
import com.gdsc.recyclr.domain.repository.RedemptionRepository
import com.gdsc.recyclr.domain.repository.ShopRepository
import com.gdsc.recyclr.screens.support.SUPPORT_CHAT_THREAD_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val repository: ShopRepository,
    private val authRepository: AuthRepository,
    private val redemptionRepository: RedemptionRepository,
    private val impactRepository: ImpactRepository,
    private val wishlistDao: WishlistDao,
    private val chatMessageDao: ChatMessageDao,
    private val badgePreferencesStore: BadgePreferencesStore,
) : ViewModel() {

    var shopItemsResponse by mutableStateOf<Response<List<ShopItem>>>(Loading)
        private set

    var redemptionHistoryResponse by mutableStateOf<Response<List<Redemption>>>(Loading)
        private set

    var redeemResponse by mutableStateOf<Response<Redemption>>(Response.Success(null))
        private set

    var pointsBalance by mutableIntStateOf(0)
        private set

    var wishlistProductIds by mutableStateOf<Set<String>>(emptySet())
        private set

    val supportUnreadCount: StateFlow<Int> = combine(
        chatMessageDao.observeThread(SUPPORT_CHAT_THREAD_ID),
        badgePreferencesStore.lastSupportThreadSeenMillis,
    ) { rows, lastSeen ->
        rows.count { !it.fromUser && it.sentAtMillis > lastSeen }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    private val uid: String get() = authRepository.currentUser?.uid ?: "guest"

    fun acknowledgeRedeemFeedback() {
        redeemResponse = Response.Success(null)
    }

    init {
        loadShopItems()
        loadHistory()
        observePoints()
        observeWishlist()
    }

    private fun observePoints() {
        viewModelScope.launch {
            impactRepository.observeUserImpact(uid).collectLatest { resp ->
                if (resp is Success) {
                    pointsBalance = resp.data?.pointsBalance ?: 0
                }
            }
        }
    }

    private fun observeWishlist() {
        viewModelScope.launch {
            wishlistDao.observeProductIds(uid).collectLatest { ids ->
                wishlistProductIds = ids.toSet()
            }
        }
    }

    fun refreshShop() {
        loadShopItems()
    }

    private fun loadShopItems() {
        viewModelScope.launch {
            val hasCached = (shopItemsResponse as? Success)?.data?.isNotEmpty() == true
            if (!hasCached) shopItemsResponse = Response.Loading
            shopItemsResponse = repository.getAllShopItems()
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            redemptionHistoryResponse = Response.Loading
            redemptionHistoryResponse = redemptionRepository.getHistory(uid)
        }
    }

    fun toggleWishlist(productId: String) {
        viewModelScope.launch {
            if (wishlistProductIds.contains(productId)) {
                wishlistDao.remove(uid, productId)
            } else {
                wishlistDao.insert(
                    CachedWishlistEntity(
                        userId = uid,
                        productId = productId,
                        addedAtMillis = System.currentTimeMillis(),
                    ),
                )
            }
        }
    }

    fun isWishlisted(productId: String): Boolean = wishlistProductIds.contains(productId)

    fun redeem(item: ShopItem) {
        viewModelScope.launch {
            redeemResponse = Response.Loading
            val result = redemptionRepository.redeem(uid, item)
            redeemResponse = result
            if (result is Success && result.data != null) {
                impactRepository.getUserImpact(uid)
            }
            loadHistory()
        }
    }
}
