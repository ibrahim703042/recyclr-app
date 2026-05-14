package com.gdsc.recyclr.screens.profile

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gdsc.recyclr.activities.MainViewModel
import com.gdsc.recyclr.screens.profile.components.ProfileContent
import com.gdsc.recyclr.screens.profile.components.RevokeAccess

@Composable
fun ProfileScreen(
    onOpenWallet: () -> Unit = {},
    onOpenBlockchainWallet: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val activity = LocalContext.current as ComponentActivity
    val mainViewModel = hiltViewModel<MainViewModel>(viewModelStoreOwner = activity)
    val isGuest by mainViewModel.guestModeEnabled.collectAsStateWithLifecycle()

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
    ) { padding ->
        ProfileContent(
            padding = padding,
            user = viewModel.currentUser,
            impactResponse = viewModel.impactResponse,
            badges = viewModel.badges,
            isGuest = isGuest,
            onGuestSignIn = { mainViewModel.setGuestModeEnabled(false) },
            onLogoutClick = { viewModel.signOut() },
            onDeleteAccountClick = { viewModel.revokeAccess() },
            onOpenWallet = onOpenWallet,
            onOpenBlockchainWallet = onOpenBlockchainWallet,
            onOpenSettings = onOpenSettings,
        )
    }

    RevokeAccess(
        scaffoldState = scaffoldState,
        coroutineScope = coroutineScope,
        signOut = { viewModel.signOut() },
    )
}
