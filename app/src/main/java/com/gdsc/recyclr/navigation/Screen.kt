package com.gdsc.recyclr.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    // Root graph
    object Onboarding: Screen("onboarding")
    object Auth: Screen("auth")
    object Main: Screen("main")

    // Auth
    object SignInScreen: Screen("signIn_screen")
    object ForgotPasswordScreen: Screen("forgotPassword_screen")
    object SignUpScreen: Screen("signUp_screen")

    // App-level destinations (outside bottom nav)
    object Results: Screen("results")
}

sealed class BottomBarPage(
    val route: String,
    val title: String,
    val icon: ImageVector,
)
{
    object Home: BottomBarPage(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home
    )
    object Scan: BottomBarPage(
        route = "scan",
        title = "Scan",
        icon = Icons.Default.PhotoCamera
    )
    object Map: BottomBarPage(
        route = "map",
        title = "Map",
        icon = Icons.Default.Place
    )
    object Shop: BottomBarPage(
        route = "shop",
        title = "Shop",
        icon = Icons.Default.ShoppingBag
    )
    object Profile: BottomBarPage(
        route = "profile",
        title = "Profile",
        icon = Icons.Default.Person
    )
}
