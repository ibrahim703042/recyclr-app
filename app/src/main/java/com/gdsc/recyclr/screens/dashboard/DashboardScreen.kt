package com.gdsc.recyclr.screens.dashboard

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gdsc.recyclr.activities.MainViewModel
import com.gdsc.recyclr.navigation.BottomBarPage
import com.gdsc.recyclr.navigation.BottomNavGraph

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

    LaunchedEffect(pendingBottomTab) {
        val route = pendingBottomTab ?: return@LaunchedEffect
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
        mainViewModel.consumePendingMainBottomTab()
    }

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
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
    val screens = listOf(
        BottomBarPage.Home,
        BottomBarPage.Scan,
        BottomBarPage.Map,
        BottomBarPage.Shop,
        BottomBarPage.Profile,
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Column(modifier = Modifier.background(Color.White)) {
        HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE0E0E0))
        NavigationBar(
            containerColor = Color.White,
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
    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
    
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
                modifier = Modifier.size(24.dp),
            )
        },
        selected = selected,
        onClick = {
            navController.navigate(screen.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color(0xFF2E7D32),
            selectedTextColor = Color(0xFF2E7D32),
            unselectedIconColor = Color(0xFF9E9E9E),
            unselectedTextColor = Color(0xFF9E9E9E),
            indicatorColor = Color.Transparent,
        ),
    )
}
