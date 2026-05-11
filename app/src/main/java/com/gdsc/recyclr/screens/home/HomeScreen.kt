package com.gdsc.recyclr.screens.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

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
    viewModel: HomeViewModel = hiltViewModel(),
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { padding ->
        HomeContent(
            padding = padding,
            impactResponse = viewModel.impactResponse,
            dashboardResponse = viewModel.dashboardResponse,
            onOpenCategory = onOpenCategory,
            onOpenScan = onOpenScan,
            onOpenChallenge = onOpenChallenge,
            onOpenLeaderboard = onOpenLeaderboard,
            onOpenCommunity = onOpenCommunity,
            onOpenWallet = onOpenWallet,
            onOpenPickup = onOpenPickup,
            onOpenMap = onOpenMap,
        )
    }
}
