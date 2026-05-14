@file:Suppress("DEPRECATION")
@file:OptIn(
    androidx.compose.animation.ExperimentalAnimationApi::class,
    androidx.compose.material.ExperimentalMaterialApi::class,
    androidx.compose.ui.ExperimentalComposeUiApi::class,
)

package com.gdsc.recyclr.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gdsc.recyclr.screens.auths.forgot_password.ForgotPasswordScreen
import com.gdsc.recyclr.screens.auths.sign_in.SignInScreen
import com.gdsc.recyclr.screens.auths.sign_up.SignUpScreen
import com.gdsc.recyclr.screens.category.CategoryDetailScreen
import com.gdsc.recyclr.screens.dashboard.MainScreen
import com.gdsc.recyclr.screens.engagement.BlockchainWalletScreen
import com.gdsc.recyclr.screens.engagement.ChallengeScreen
import com.gdsc.recyclr.screens.engagement.CommunityScreen
import com.gdsc.recyclr.screens.engagement.LeaderboardScreen
import com.gdsc.recyclr.screens.engagement.PickupScreen
import com.gdsc.recyclr.screens.engagement.WalletScreen
import com.gdsc.recyclr.screens.home.HomeScreen
import com.gdsc.recyclr.screens.map.MapScreen
import com.gdsc.recyclr.screens.notifications.NotificationsScreen
import com.gdsc.recyclr.screens.onboarding.OnboardingScreen
import com.gdsc.recyclr.screens.profile.ProfileScreen
import com.gdsc.recyclr.screens.results.ResultsScreen
import com.gdsc.recyclr.screens.scan.ScanScreen
import com.gdsc.recyclr.screens.settings.AboutRecyclrScreen
import com.gdsc.recyclr.screens.settings.PersonalInformationScreen
import com.gdsc.recyclr.screens.settings.SettingsScreen
import com.gdsc.recyclr.screens.shop.ShopScreen
import com.gdsc.recyclr.screens.support.HelpWebViewScreen
import com.gdsc.recyclr.screens.support.SupportChatScreen

private val tabEnter = fadeIn(animationSpec = tween(240, delayMillis = 20)) +
    slideInHorizontally(animationSpec = tween(260)) { it / 14 }

private val tabExit = fadeOut(animationSpec = tween(180))

private val tabPopEnter = fadeIn(animationSpec = tween(240)) +
    slideInHorizontally(animationSpec = tween(260)) { it / 14 }

private val tabPopExit = fadeOut(animationSpec = tween(180)) +
    slideOutHorizontally(animationSpec = tween(220)) { it / 14 }

@Composable
fun RootNavGraph(
    isSignedOut: Boolean,
    isOnboardingCompleted: Boolean,
    onOnboardingCompleted: () -> Unit,
    isGuestModeEnabled: Boolean,
    onGuestModeEnabled: (Boolean) -> Unit,
    onRequestOpenShopFromResults: () -> Unit = {},
    onRequestOpenMapFromResults: () -> Unit = {},
    navController: NavHostController = rememberNavController(),
) {
    val startDestination = when {
        !isOnboardingCompleted -> Screen.Onboarding.route
        isSignedOut && !isGuestModeEnabled -> Screen.SignInScreen.route
        else -> Screen.Main.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    onOnboardingCompleted()
                    navController.navigate(Screen.SignInScreen.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
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
                },
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
                },
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onOpenHelpCenter = { navController.navigate(Screen.SettingsHelp.route) },
                onOpenAbout = { navController.navigate(Screen.SettingsAbout.route) },
                onOpenPersonalInformation = { navController.navigate(Screen.SettingsPersonalInfo.route) },
            )
        }

        composable(Screen.SettingsHelp.route) {
            HelpWebViewScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.SettingsAbout.route) {
            AboutRecyclrScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.SettingsPersonalInfo.route) {
            PersonalInformationScreen(onBack = { navController.popBackStack() })
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

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    navigateToResults: (itemType: String, points: Int, co2SavedGrams: Float, destination: String) -> Unit,
    navigateToSettings: () -> Unit = {},
) {
    NavHost(
        navController = navController,
        startDestination = BottomBarPage.Home.route,
        enterTransition = { tabEnter },
        exitTransition = { tabExit },
        popEnterTransition = { tabPopEnter },
        popExitTransition = { tabPopExit },
    ) {
        composable(route = BottomBarPage.Home.route) {
            HomeScreen(
                onOpenCategory = { categoryKey ->
                    navController.navigateToFeature(FeatureRoute.categoryDetail(categoryKey))
                },
                onOpenScan = {
                    navController.navigateToMainTab(BottomBarPage.Scan.route)
                },
                onOpenChallenge = { navController.navigateToFeature(FeatureRoute.Challenges) },
                onOpenLeaderboard = { navController.navigateToFeature(FeatureRoute.Leaderboard) },
                onOpenCommunity = { navController.navigateToFeature(FeatureRoute.Community) },
                onOpenWallet = { navController.navigateToFeature(FeatureRoute.Wallet) },
                onOpenPickup = { navController.navigateToFeature(FeatureRoute.Pickup) },
                onOpenMap = {
                    navController.navigateToMainTab(BottomBarPage.Map.route)
                },
                onOpenProfile = {
                    navController.navigateToMainTab(BottomBarPage.Profile.route)
                },
                onOpenNotifications = {
                    navController.navigateToFeature(FeatureRoute.Notifications)
                },
            )
        }
        composable(route = BottomBarPage.Scan.route) {
            ScanScreen(
                navigateToResults = navigateToResults,
            )
        }
        composable(route = BottomBarPage.Map.route) {
            MapScreen(
                onRequestPickup = { navController.navigateToFeature(FeatureRoute.Pickup) },
            )
        }
        composable(route = BottomBarPage.Shop.route) {
            ShopScreen(
                onOpenSupportChat = { navController.navigateToFeature(FeatureRoute.SupportChat) },
            )
        }
        composable(route = BottomBarPage.Profile.route) {
            ProfileScreen(
                onOpenWallet = { navController.navigateToFeature(FeatureRoute.Wallet) },
                onOpenBlockchainWallet = { navController.navigateToFeature(FeatureRoute.BlockchainWallet) },
                onOpenSettings = navigateToSettings,
            )
        }
        composable(
            route = FeatureRoute.CategoryDetail,
            arguments = listOf(navArgument("categoryKey") { type = NavType.StringType }),
        ) { entry ->
            CategoryDetailScreen(
                categoryKey = entry.arguments?.getString("categoryKey").orEmpty(),
                onBack = { navController.popBackStack() },
            )
        }
        composable(FeatureRoute.Challenges) {
            ChallengeScreen(onBack = { navController.popBackStack() })
        }
        composable(FeatureRoute.Leaderboard) {
            LeaderboardScreen(onBack = { navController.popBackStack() })
        }
        composable(FeatureRoute.Community) {
            CommunityScreen(onBack = { navController.popBackStack() })
        }
        composable(FeatureRoute.Wallet) {
            WalletScreen(
                onBack = { navController.popBackStack() },
                onOpenBlockchainWallet = { navController.navigateToFeature(FeatureRoute.BlockchainWallet) },
            )
        }
        composable(FeatureRoute.BlockchainWallet) {
            BlockchainWalletScreen(onBack = { navController.popBackStack() })
        }
        composable(FeatureRoute.Pickup) {
            PickupScreen(onBack = { navController.popBackStack() })
        }
        composable(FeatureRoute.Notifications) {
            NotificationsScreen(
                onBack = { navController.popBackStack() },
                onOpenHelpCenter = { navController.navigateToFeature(FeatureRoute.HelpCenter) },
            )
        }
        composable(FeatureRoute.SupportChat) {
            SupportChatScreen(
                onBack = { navController.popBackStack() },
            )
        }
        composable(FeatureRoute.HelpCenter) {
            HelpWebViewScreen(onBack = { navController.popBackStack() })
        }
    }
}
