package com.gdsc.recyclr.screens.profile

import androidx.compose.foundation.background
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.gdsc.recyclr.components.composable.TopBar
import com.gdsc.recyclr.screens.profile.components.ProfileContent
import com.gdsc.recyclr.screens.profile.components.RevokeAccess
import com.gdsc.recyclr.ui.theme.Gray_color

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.background(Gray_color),
        topBar = {
            TopBar(
                title = "Profil",
                signOut = {
                    viewModel.signOut()
                },
                revokeAccess = {
                    viewModel.revokeAccess()
                }
            )
        },
        content = { padding ->
            ProfileContent(
                padding = padding,
                user = viewModel.currentUser,
                impactResponse = viewModel.impactResponse
            )
        },
        scaffoldState = scaffoldState
    )

    RevokeAccess(
        scaffoldState = scaffoldState,
        coroutineScope = coroutineScope,
        signOut = {
            viewModel.signOut()
        }
    )
}
