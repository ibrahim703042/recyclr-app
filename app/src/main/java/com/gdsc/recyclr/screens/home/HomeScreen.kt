package com.gdsc.recyclr.screens.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.gdsc.recyclr.domain.model.UserRole

@Composable
fun HomeScreen(
    onOpenCategory: (String) -> Unit,
    onOpenScan: () -> Unit,
    onOpenChallenge: () -> Unit,
    onOpenLeaderboard: () -> Unit,
    onOpenCommunity: () -> Unit,
    onOpenWallet: () -> Unit,
    onOpenPickup: () -> Unit,
    onOpenMap: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenCollectorDashboard: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val unreadNotificationCount by viewModel.unreadNotificationCount.collectAsStateWithLifecycle()
    val photoUrl = viewModel.currentUser?.photoUrl?.takeIf { !it.isNullOrBlank() }
    val userRole = viewModel.currentUser?.role ?: UserRole.USER

    HomeContent(
        padding = PaddingValues(),
        impactResponse = viewModel.impactResponse,
        dashboardResponse = viewModel.dashboardResponse,
        userName = viewModel.welcomeName,
        userRole = userRole,
        latestLiveActivity = viewModel.latestLiveActivity,
        photoUrl = photoUrl,
        unreadNotificationCount = unreadNotificationCount,
        onRefresh = { viewModel.refreshDashboard() },
        onOpenCategory = onOpenCategory,
        onOpenScan = onOpenScan,
        onOpenChallenge = onOpenChallenge,
        onOpenLeaderboard = onOpenLeaderboard,
        onOpenCommunity = onOpenCommunity,
        onOpenWallet = onOpenWallet,
        onOpenPickup = onOpenPickup,
        onOpenMap = onOpenMap,
        onOpenProfile = onOpenProfile,
        onOpenNotifications = onOpenNotifications,
        onOpenCollectorDashboard = onOpenCollectorDashboard,
    )
}
