@file:Suppress("DEPRECATION")

package com.gdsc.recyclr.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import com.gdsc.recyclr.navigation.RootNavGraph
import com.gdsc.recyclr.ui.preferences.AppAppearanceState
import com.gdsc.recyclr.ui.preferences.LocalAppAppearance
import com.gdsc.recyclr.ui.preferences.resolveIsDarkTheme
import com.gdsc.recyclr.ui.theme.RecyclrTheme
import com.gdsc.recyclr.util.AppLocaleManager
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint

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
            RecyclrAppRoot(viewModel = viewModel)
        }
    }
}

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalAnimationApi::class,
    ExperimentalComposeUiApi::class,
    ExperimentalPagerApi::class,
)
@Composable
private fun RecyclrAppRoot(viewModel: MainViewModel) {
    val isSignedOut = viewModel.getAuthState().collectAsState().value
    val onboardingCompleted = viewModel.onboardingCompleted.collectAsState().value
    val guestModeEnabled = viewModel.guestModeEnabled.collectAsState().value
    val themeMode by viewModel.themeMode.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()
    val isDarkTheme = resolveIsDarkTheme(themeMode)

    LaunchedEffect(appLanguage) {
        AppLocaleManager.apply(appLanguage)
    }

    val appearance = remember(themeMode, appLanguage, isDarkTheme) {
        AppAppearanceState(
            themeMode = themeMode,
            language = appLanguage,
            isDarkTheme = isDarkTheme,
            setThemeMode = viewModel::setThemeMode,
            setLanguage = viewModel::setAppLanguage,
            cycleThemeMode = viewModel::cycleThemeMode,
        )
    }

    CompositionLocalProvider(LocalAppAppearance provides appearance) {
        RecyclrTheme(darkTheme = isDarkTheme) {
            RootNavGraph(
                isSignedOut = isSignedOut,
                isOnboardingCompleted = onboardingCompleted,
                onOnboardingCompleted = { viewModel.setOnboardingCompleted() },
                isGuestModeEnabled = guestModeEnabled,
                onGuestModeEnabled = { enabled -> viewModel.setGuestModeEnabled(enabled) },
                onRequestOpenShopFromResults = { viewModel.requestOpenShopTab() },
            )
        }
    }
}
