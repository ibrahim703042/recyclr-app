/**
 * Routes et helpers de navigation (graphe racine + onglets + features).
 * Les [NavHost] sont définis dans [NavHosts].
 */
package com.gdsc.recyclr.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.gdsc.recyclr.R

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Auth : Screen("auth")
    object Main : Screen("main")

    object SignInScreen : Screen("signIn_screen")
    object ForgotPasswordScreen : Screen("forgotPassword_screen")
    object SignUpScreen : Screen("signUp_screen")

    object Results : Screen("results")
    object Settings : Screen("settings")
    object SettingsHelp : Screen("settings_help")
    object SettingsAbout : Screen("settings_about")
    object SettingsPersonalInfo : Screen("settings_personal_info")
}

sealed class BottomBarPage(
    val route: String,
    @StringRes val titleRes: Int,
    val icon: ImageVector,
) {
    object Home : BottomBarPage(
        route = "home",
        titleRes = R.string.nav_home,
        icon = Icons.Default.Home,
    )

    object Scan : BottomBarPage(
        route = "scan",
        titleRes = R.string.nav_scan,
        icon = Icons.Default.PhotoCamera,
    )

    object Map : BottomBarPage(
        route = "map",
        titleRes = R.string.nav_stats,
        icon = Icons.Default.BarChart,
    )

    object Shop : BottomBarPage(
        route = "shop",
        titleRes = R.string.nav_rewards,
        icon = Icons.Default.EmojiEvents,
    )

    object Profile : BottomBarPage(
        route = "profile",
        titleRes = R.string.nav_profile,
        icon = Icons.Default.Person,
    )
}

object FeatureRoute {
    const val CategoryDetail = "category/{categoryKey}"

    const val Challenges = "feature/challenges"
    const val Leaderboard = "feature/leaderboard"
    const val Community = "feature/community"
    const val Wallet = "feature/wallet"
    const val BlockchainWallet = "feature/blockchain_wallet"
    const val Pickup = "feature/pickup"
    const val AdminDashboard = "feature/admin_dashboard"
    const val CollectorDashboard = "feature/collector_dashboard"
    const val Notifications = "feature/notifications"
    const val SupportChat = "feature/support_chat"
    const val HelpCenter = "feature/help_center"

    fun categoryDetail(categoryKey: String): String = "category/$categoryKey"
}

/** Onglets : pile partagée, état sauvegardé. */
fun NavHostController.navigateToMainTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

/** Écrans empilés au-dessus des onglets (une instance logique, retour arrière). */
fun NavHostController.navigateToFeature(route: String) {
    navigate(route) {
        launchSingleTop = true
    }
}
