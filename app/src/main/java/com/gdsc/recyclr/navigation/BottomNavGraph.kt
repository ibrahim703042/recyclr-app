package com.gdsc.recyclr.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gdsc.recyclr.screens.category.CategoryDetailScreen
import com.gdsc.recyclr.screens.engagement.ChallengeScreen
import com.gdsc.recyclr.screens.engagement.CommunityScreen
import com.gdsc.recyclr.screens.engagement.LeaderboardScreen
import com.gdsc.recyclr.screens.engagement.PickupScreen
import com.gdsc.recyclr.screens.engagement.WalletScreen
import com.gdsc.recyclr.screens.home.HomeScreen
import com.gdsc.recyclr.screens.map.MapScreen
import com.gdsc.recyclr.screens.profile.ProfileScreen
import com.gdsc.recyclr.screens.scan.ScanScreen
import com.gdsc.recyclr.screens.shop.ShopScreen

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    navigateToResults: (itemType: String, points: Int, co2SavedGrams: Float, destination: String) -> Unit,
    navigateToSettings: () -> Unit = {},
) {
    NavHost(
        navController = navController,
        startDestination = BottomBarPage.Home.route,
    ) {
        composable(route = BottomBarPage.Home.route) {
            HomeScreen(
                onOpenCategory = { categoryKey ->
                    navController.navigate("category/$categoryKey")
                },
                onOpenScan = {
                    navController.navigate(BottomBarPage.Scan.route) {
                        popUpTo(navController.graph.findStartDestination().id)
                        launchSingleTop = true
                    }
                },
                onOpenChallenge = { navController.navigate(FeatureRoute.Challenges) },
                onOpenLeaderboard = { navController.navigate(FeatureRoute.Leaderboard) },
                onOpenCommunity = { navController.navigate(FeatureRoute.Community) },
                onOpenWallet = { navController.navigate(FeatureRoute.Wallet) },
                onOpenPickup = { navController.navigate(FeatureRoute.Pickup) },
                onOpenMap = {
                    navController.navigate(BottomBarPage.Map.route) {
                        popUpTo(navController.graph.findStartDestination().id)
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(route = BottomBarPage.Scan.route) {
            ScanScreen(
                navigateToResults = navigateToResults,
            )
        }
        composable(route = BottomBarPage.Map.route) {
            MapScreen()
        }
        composable(route = BottomBarPage.Shop.route) {
            ShopScreen()
        }
        composable(route = BottomBarPage.Profile.route) {
            ProfileScreen(
                onOpenWallet = { navController.navigate(FeatureRoute.Wallet) },
                onOpenSettings = navigateToSettings,
            )
        }
        composable(
            route = "category/{categoryKey}",
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
            WalletScreen(onBack = { navController.popBackStack() })
        }
        composable(FeatureRoute.Pickup) {
            PickupScreen(onBack = { navController.popBackStack() })
        }
    }
}
