package com.gdsc.recyclr.screens.dashboard

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import com.gdsc.recyclr.ui.theme.LeafGreen

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@ExperimentalMaterialApi
@Composable
fun MainScreen(
    navigateToResults: (itemType: String, points: Int, co2SavedGrams: Float, destination: String) -> Unit = { _, _, _, _ -> },
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
    ) {
        BottomNavGraph(
            navController = navController,
            navigateToResults = navigateToResults,
        )
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

    BottomNavigation(
        backgroundColor = Color.White,
        elevation = 8.dp,
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

@Composable
fun RowScope.AddItem(
    screen: BottomBarPage,
    currentDestination: NavDestination?,
    navController: NavHostController,
) {
    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
    BottomNavigationItem(
        label = {
            Text(
                text = stringResource(screen.titleRes),
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            )
        },
        icon = {
            Icon(
                imageVector = screen.icon,
                contentDescription = "Navigation Icon",
            )
        },
        selected = selected,
        selectedContentColor = LeafGreen,
        unselectedContentColor = LocalContentColor.current.copy(alpha = ContentAlpha.disabled),
        onClick = {
            navController.navigate(screen.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        },
    )
}
