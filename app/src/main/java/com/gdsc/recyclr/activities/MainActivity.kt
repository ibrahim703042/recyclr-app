@file:Suppress("DEPRECATION")

package com.gdsc.recyclr.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.ExperimentalComposeUiApi
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.collectAsState
import com.gdsc.recyclr.navigation.RootNavGraph
import com.gdsc.recyclr.ui.theme.RecyclrTheme
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalPagerApi
@ExperimentalMaterialApi
@AndroidEntryPoint
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecyclrTheme {
                val isSignedOut = viewModel.getAuthState().collectAsState().value
                val onboardingCompleted = viewModel.onboardingCompleted.collectAsState().value
                val guestModeEnabled = viewModel.guestModeEnabled.collectAsState().value

                RootNavGraph(
                    isSignedOut = isSignedOut,
                    isOnboardingCompleted = onboardingCompleted,
                    onOnboardingCompleted = { viewModel.setOnboardingCompleted() },
                    isGuestModeEnabled = guestModeEnabled,
                    onGuestModeEnabled = { enabled -> viewModel.setGuestModeEnabled(enabled) },
                    onRequestOpenShopFromResults = { viewModel.requestOpenShopTab() }
                )
            }
        }
    }
}