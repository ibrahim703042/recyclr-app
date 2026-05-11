package com.gdsc.recyclr.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.gdsc.recyclr.R

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
    @StringRes val titleRes: Int,
    val icon: ImageVector,
)
{
    object Home: BottomBarPage(
        route = "home",
        titleRes = R.string.nav_home,
        icon = Icons.Default.Home
    )
    object Scan: BottomBarPage(
        route = "scan",
        titleRes = R.string.nav_scan,
        icon = Icons.Default.PhotoCamera
    )
    object Map: BottomBarPage(
        route = "map",
        titleRes = R.string.nav_map,
        icon = Icons.Default.Place
    )
    object Shop: BottomBarPage(
        route = "shop",
        titleRes = R.string.nav_shop,
        icon = Icons.Default.ShoppingBag
    )
    object Profile: BottomBarPage(
        route = "profile",
        titleRes = R.string.nav_profile,
        icon = Icons.Default.Person
    )
}
