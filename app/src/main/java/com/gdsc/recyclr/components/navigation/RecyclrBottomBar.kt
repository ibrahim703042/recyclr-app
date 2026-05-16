package com.gdsc.recyclr.components.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.gdsc.recyclr.navigation.BottomBarPage
import com.gdsc.recyclr.navigation.navigateToMainTab
import com.gdsc.recyclr.ui.theme.GreenHero

@Composable
fun RecyclrBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val scanSelected = currentDestination?.hierarchy?.any { it.route == BottomBarPage.Scan.route } == true

    Surface(
        color = GreenHero.NavBar,
        shadowElevation = 12.dp,
        tonalElevation = 0.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RecyclrBottomNavItem(
                    screen = BottomBarPage.Home,
                    currentDestination = currentDestination,
                    onClick = { navController.navigateToMainTab(BottomBarPage.Home.route) },
                    modifier = Modifier.weight(1f),
                )
                RecyclrBottomNavItem(
                    screen = BottomBarPage.Map,
                    currentDestination = currentDestination,
                    onClick = { navController.navigateToMainTab(BottomBarPage.Map.route) },
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(72.dp))
                RecyclrBottomNavItem(
                    screen = BottomBarPage.Shop,
                    currentDestination = currentDestination,
                    onClick = { navController.navigateToMainTab(BottomBarPage.Shop.route) },
                    modifier = Modifier.weight(1f),
                )
                RecyclrBottomNavItem(
                    screen = BottomBarPage.Profile,
                    currentDestination = currentDestination,
                    onClick = { navController.navigateToMainTab(BottomBarPage.Profile.route) },
                    modifier = Modifier.weight(1f),
                )
            }

            RecyclrScanFab(
                selected = scanSelected,
                onClick = { navController.navigateToMainTab(BottomBarPage.Scan.route) },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-22).dp),
            )
        }
    }
}

@Composable
private fun RecyclrBottomNavItem(
    screen: BottomBarPage,
    currentDestination: NavDestination?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.06f else 1f,
        animationSpec = tween(200),
        label = "navIconScale",
    )
    val tint = if (selected) GreenHero.Primary else GreenHero.NavInactive

    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
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
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = tint,
        )
    }
}

@Composable
private fun RecyclrScanFab(
    selected: Boolean,
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
            .size(64.dp)
            .shadow(8.dp, CircleShape)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = CircleShape,
        color = GreenHero.Primary,
        tonalElevation = 0.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.CenterFocusStrong,
                contentDescription = stringResource(BottomBarPage.Scan.titleRes),
                tint = GreenHero.OnPrimary,
                modifier = Modifier.size(30.dp),
            )
        }
    }
}
