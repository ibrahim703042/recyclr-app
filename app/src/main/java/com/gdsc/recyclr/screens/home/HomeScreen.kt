package com.gdsc.recyclr.screens.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

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
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val unreadNotificationCount by viewModel.unreadNotificationCount.collectAsStateWithLifecycle()
    val photoUrl = viewModel.currentUser?.photoUrl?.takeIf { !it.isNullOrBlank() }

    HomeContent(
        padding = PaddingValues(),
        impactResponse = viewModel.impactResponse,
        dashboardResponse = viewModel.dashboardResponse,
        userName = viewModel.welcomeName,
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
    )
}
