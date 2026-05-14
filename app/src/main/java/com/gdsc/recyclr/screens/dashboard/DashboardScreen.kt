package com.gdsc.recyclr.screens.dashboard

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween as animTween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gdsc.recyclr.activities.MainViewModel
import com.gdsc.recyclr.navigation.BottomBarPage
import com.gdsc.recyclr.navigation.BottomNavGraph
import com.gdsc.recyclr.navigation.navigateToFeature
import com.gdsc.recyclr.navigation.navigateToMainTab

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    navigateToResults: (itemType: String, points: Int, co2SavedGrams: Float, destination: String) -> Unit = { _, _, _, _ -> },
    navigateToSettings: () -> Unit = {},
) {
    val navController = rememberNavController()
    val activity = LocalContext.current as ComponentActivity
    val mainViewModel = hiltViewModel<MainViewModel>(viewModelStoreOwner = activity)
    val pendingBottomTab by mainViewModel.pendingMainBottomTabRoute.collectAsStateWithLifecycle()
    val pendingFeature by mainViewModel.pendingFeatureRoute.collectAsStateWithLifecycle()

    LaunchedEffect(pendingBottomTab, pendingFeature) {
        val tab = pendingBottomTab
        val feat = pendingFeature
        if (tab != null) {
            navController.navigateToMainTab(tab)
            mainViewModel.consumePendingMainBottomTab()
        }
        if (feat != null) {
            navController.navigateToFeature(feat)
            mainViewModel.consumePendingFeatureRoute()
        }
    }

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
    ) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding),
            color = MaterialTheme.colorScheme.background,
        ) {
            BottomNavGraph(
                navController = navController,
                navigateToResults = navigateToResults,
                navigateToSettings = navigateToSettings
            )
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val scheme = MaterialTheme.colorScheme
    val screens = listOf(
        BottomBarPage.Home,
        BottomBarPage.Scan,
        BottomBarPage.Map,
        BottomBarPage.Shop,
        BottomBarPage.Profile,
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Column(modifier = Modifier.background(scheme.surface)) {
        HorizontalDivider(thickness = 0.5.dp, color = scheme.outline.copy(alpha = 0.35f))
        NavigationBar(
            containerColor = scheme.surface,
            tonalElevation = 0.dp,
        ) {
            screens.forEach { screen ->
                AddItem(
                    screen = screen,
                    currentDestination = currentDestination,
                    navController = navController,
                )
            }
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomBarPage,
    currentDestination: NavDestination?,
    navController: NavHostController,
) {
    val scheme = MaterialTheme.colorScheme
    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.08f else 1f,
        animationSpec = animTween(durationMillis = 220),
        label = "navIconScale",
    )

    NavigationBarItem(
        label = {
            Text(
                text = stringResource(screen.titleRes),
                fontSize = 10.sp,
                maxLines = 1,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            )
        },
        icon = {
            Icon(
                imageVector = screen.icon,
                contentDescription = screen.route,
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer {
                        scaleX = iconScale
                        scaleY = iconScale
                    },
            )
        },
        selected = selected,
        onClick = {
            navController.navigateToMainTab(screen.route)
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = scheme.primary,
            selectedTextColor = scheme.primary,
            unselectedIconColor = scheme.onSurfaceVariant,
            unselectedTextColor = scheme.onSurfaceVariant,
            indicatorColor = scheme.primaryContainer.copy(alpha = 0.42f),
        ),
    )
}
