package com.gdsc.recyclr.components.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.gdsc.recyclr.navigation.BottomBarPage
import com.gdsc.recyclr.navigation.navigateToMainTab

private val NavBarHeight = 64.dp
private val ScanFabSize = 64.dp
private val ScanFabLift = 22.dp
private val CenterSlotWidth = 72.dp

@Composable
fun RecyclrBottomBar(navController: NavHostController) {
    val colors = rememberBottomBarThemeColors()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val hierarchyRoutes = currentDestination?.hierarchy
        ?.mapNotNull { it.route }
        ?.toList()
        .orEmpty()
    val scanSelected = RecyclrBottomBarDefaults.isScanSelected(hierarchyRoutes)

    Surface(
        color = colors.barBackground,
        shadowElevation = colors.barShadowElevation,
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
        ) {
            HorizontalDivider(color = colors.divider, thickness = 0.5.dp)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(NavBarHeight + ScanFabLift),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(NavBarHeight)
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RecyclrBottomNavItem(
                        screen = BottomBarPage.Home,
                        selected = RecyclrBottomBarDefaults.isTabSelected(
                            BottomBarPage.Home.route,
                            hierarchyRoutes,
                        ),
                        colors = colors,
                        onClick = { navController.navigateToMainTab(BottomBarPage.Home.route) },
                        modifier = Modifier.weight(1f),
                    )
                    RecyclrBottomNavItem(
                        screen = BottomBarPage.Shop,
                        selected = RecyclrBottomBarDefaults.isTabSelected(
                            BottomBarPage.Shop.route,
                            hierarchyRoutes,
                        ),
                        colors = colors,
                        onClick = { navController.navigateToMainTab(BottomBarPage.Shop.route) },
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(Modifier.width(CenterSlotWidth))
                    RecyclrBottomNavItem(
                        screen = BottomBarPage.Map,
                        selected = RecyclrBottomBarDefaults.isTabSelected(
                            BottomBarPage.Map.route,
                            hierarchyRoutes,
                        ),
                        colors = colors,
                        onClick = { navController.navigateToMainTab(BottomBarPage.Map.route) },
                        modifier = Modifier.weight(1f),
                    )
                    RecyclrBottomNavItem(
                        screen = BottomBarPage.Profile,
                        selected = RecyclrBottomBarDefaults.isTabSelected(
                            BottomBarPage.Profile.route,
                            hierarchyRoutes,
                        ),
                        colors = colors,
                        onClick = { navController.navigateToMainTab(BottomBarPage.Profile.route) },
                        modifier = Modifier.weight(1f),
                    )
                }

                RecyclrScanFab(
                    selected = scanSelected,
                    colors = colors,
                    onClick = { navController.navigateToMainTab(BottomBarPage.Scan.route) },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = 0.dp),
                )
            }
        }
    }
}

@Composable
private fun RecyclrBottomNavItem(
    screen: BottomBarPage,
    selected: Boolean,
    colors: BottomBarThemeColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.06f else 1f,
        animationSpec = tween(200),
        label = "navIconScale",
    )
    val tint = if (selected) colors.activeTint else colors.inactiveTint

    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false, radius = 28.dp),
                onClick = onClick,
            )
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Icon(
            imageVector = screen.icon,
            contentDescription = stringResource(screen.titleRes),
            tint = tint,
            modifier = Modifier
                .size(24.dp)
                .graphicsLayer {
                    scaleX = iconScale
                    scaleY = iconScale
                },
        )
        Text(
            text = stringResource(screen.titleRes),
            fontSize = 10.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = tint,
        )
    }
}

@Composable
private fun RecyclrScanFab(
    selected: Boolean,
    colors: BottomBarThemeColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.05f else 1f,
        animationSpec = tween(200),
        label = "scanFabScale",
    )

    Surface(
        onClick = onClick,
        modifier = modifier
            .size(ScanFabSize)
            .shadow(colors.fabShadowElevation, CircleShape, clip = false)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = CircleShape,
        color = colors.fabBackground,
        border = BorderStroke(4.dp, colors.fabBorder),
        tonalElevation = 0.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = stringResource(BottomBarPage.Scan.titleRes),
                tint = colors.fabContent,
                modifier = Modifier.size(30.dp),
            )
        }
    }
}
