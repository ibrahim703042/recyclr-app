package com.gdsc.recyclr.screens.dashboard

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.gdsc.recyclr.activities.MainViewModel
import com.gdsc.recyclr.components.navigation.RecyclrBottomBar
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
        bottomBar = { RecyclrBottomBar(navController = navController) },
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
