@file:Suppress("DEPRECATION")

package com.gdsc.recyclr.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gdsc.recyclr.screens.auths.forgot_password.ForgotPasswordScreen
import com.gdsc.recyclr.screens.auths.sign_in.SignInScreen
import com.gdsc.recyclr.screens.auths.sign_up.SignUpScreen
import com.gdsc.recyclr.screens.dashboard.MainScreen
import com.gdsc.recyclr.screens.onboarding.OnboardingScreen
import com.gdsc.recyclr.screens.results.ResultsScreen
import com.gdsc.recyclr.screens.settings.SettingsScreen

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun RootNavGraph(
    isSignedOut: Boolean,
    isOnboardingCompleted: Boolean,
    onOnboardingCompleted: () -> Unit,
    isGuestModeEnabled: Boolean,
    onGuestModeEnabled: (Boolean) -> Unit,
    onRequestOpenShopFromResults: () -> Unit = {},
    onRequestOpenMapFromResults: () -> Unit = {},
    navController: NavHostController = rememberNavController()
) {
    val startDestination = when {
        !isOnboardingCompleted -> Screen.Onboarding.route
        isSignedOut && !isGuestModeEnabled -> Screen.SignInScreen.route
        else -> Screen.Main.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    onOnboardingCompleted()
                    navController.navigate(Screen.SignInScreen.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.SignInScreen.route) {
            SignInScreen(
                navigateToForgotPasswordScreen = {
                    navController.navigate(Screen.ForgotPasswordScreen.route)
                },
                navigateToSignUpScreen = {
                    navController.navigate(Screen.SignUpScreen.route)
                },
                continueAsGuest = {
                    onGuestModeEnabled(true)
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.SignInScreen.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ForgotPasswordScreen.route) {
            ForgotPasswordScreen(navigateBack = { navController.popBackStack() })
        }

        composable(Screen.SignUpScreen.route) {
            SignUpScreen(navigateBack = { navController.popBackStack() })
        }

        composable(Screen.Main.route) {
            MainScreen(
                navigateToResults = { itemType, points, co2SavedGrams, destination ->
                    val safeItemType = android.net.Uri.encode(itemType)
                    val safeDestination = android.net.Uri.encode(destination)
                    navController.navigate("${Screen.Results.route}/$safeItemType/$points/$co2SavedGrams/$safeDestination")
                },
                navigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }

        composable("${Screen.Results.route}/{itemType}/{points}/{co2SavedGrams}/{destination}") { entry ->
            val itemType = entry.arguments?.getString("itemType").orEmpty()
            val points = entry.arguments?.getString("points")?.toIntOrNull() ?: 0
            val co2SavedGrams = entry.arguments?.getString("co2SavedGrams")?.toFloatOrNull() ?: 0f
            val destination = entry.arguments?.getString("destination").orEmpty()

            ResultsScreen(
                itemType = itemType,
                points = points,
                co2SavedGrams = co2SavedGrams,
                destination = destination,
                onRecycleAgain = { navController.popBackStack() },
                onViewRewards = {
                    onRequestOpenShopFromResults()
                    navController.popBackStack()
                },
                onOpenMap = {
                    onRequestOpenMapFromResults()
                    navController.popBackStack()
                },
            )
        }
    }

    LaunchedEffect(isSignedOut, isOnboardingCompleted) {
        val destination = when {
            !isOnboardingCompleted -> Screen.Onboarding.route
            isSignedOut && !isGuestModeEnabled -> Screen.SignInScreen.route
            else -> Screen.Main.route
        }
        if (!isSignedOut && isGuestModeEnabled) {
            onGuestModeEnabled(false)
        }
        if (navController.currentDestination?.route != destination) {
            navController.navigate(destination) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
}

