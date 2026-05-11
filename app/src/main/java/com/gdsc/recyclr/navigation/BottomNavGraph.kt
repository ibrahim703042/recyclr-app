package com.gdsc.recyclr.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gdsc.recyclr.screens.category.CategoryDetailScreen
import com.gdsc.recyclr.screens.home.HomeScreen
import com.gdsc.recyclr.screens.profile.ProfileScreen
import com.gdsc.recyclr.screens.scan.ScanScreen
import com.gdsc.recyclr.screens.shop.ShopScreen

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    navigateToResults: (itemType: String, points: Int, co2SavedGrams: Float, destination: String) -> Unit,
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
            )
        }
        composable(route = BottomBarPage.Scan.route) {
            ScanScreen(
                navigateToResults = navigateToResults,
            )
        }
        composable(route = BottomBarPage.Shop.route) {
            ShopScreen()
        }
        composable(route = BottomBarPage.Profile.route) {
            ProfileScreen()
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
    }
}
