package com.gdsc.recyclr.components.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gdsc.recyclr.navigation.BottomBarPage
import com.gdsc.recyclr.ui.theme.GreenHero
import com.gdsc.recyclr.ui.theme.isRecyclrDarkTheme

data class BottomBarThemeColors(
    val barBackground: Color,
    val divider: Color,
    val activeTint: Color,
    val inactiveTint: Color,
    val fabBackground: Color,
    val fabContent: Color,
    val fabBorder: Color,
    val fabShadowElevation: Dp,
    val barShadowElevation: Dp,
)

fun bottomBarThemeColors(isDark: Boolean): BottomBarThemeColors = if (isDark) {
    BottomBarThemeColors(
        barBackground = Color(0xFF232623),
        divider = Color(0xFF3A403A),
        activeTint = Color(0xFF7DDA92),
        inactiveTint = Color(0xFF9BA89E),
        fabBackground = Color(0xFF4CAF7A),
        fabContent = Color(0xFF0D2818),
        fabBorder = Color(0xFF232623),
        fabShadowElevation = 6.dp,
        barShadowElevation = 0.dp,
    )
} else {
    BottomBarThemeColors(
        barBackground = GreenHero.NavBar,
        divider = GreenHero.Divider,
        activeTint = GreenHero.Primary,
        inactiveTint = GreenHero.NavInactive,
        fabBackground = GreenHero.Primary,
        fabContent = GreenHero.OnPrimary,
        fabBorder = GreenHero.NavBar,
        fabShadowElevation = 10.dp,
        barShadowElevation = 8.dp,
    )
}

@Composable
fun rememberBottomBarThemeColors(): BottomBarThemeColors =
    bottomBarThemeColors(isRecyclrDarkTheme())

object RecyclrBottomBarDefaults {
    val sideTabRoutes: List<String> = listOf(
        BottomBarPage.Home.route,
        BottomBarPage.Shop.route,
        BottomBarPage.Map.route,
        BottomBarPage.Profile.route,
    )

    fun isTabSelected(tabRoute: String, hierarchyRoutes: Iterable<String>): Boolean =
        hierarchyRoutes.any { it == tabRoute }

    fun isScanSelected(hierarchyRoutes: Iterable<String>): Boolean =
        hierarchyRoutes.any { it == BottomBarPage.Scan.route }
}
