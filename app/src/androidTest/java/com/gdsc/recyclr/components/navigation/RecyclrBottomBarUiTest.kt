package com.gdsc.recyclr.components.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import androidx.navigation.testing.TestNavHostController
import com.gdsc.recyclr.navigation.BottomBarPage
import com.gdsc.recyclr.test.RecyclrTestHarness
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class RecyclrBottomBarUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun bottomBar_lightMode_showsAllTabsAndScanAction() {
        composeTestRule.setContent {
            val navController = rememberTestBottomBarNavController()
            RecyclrTestHarness(isDark = false) {
                RecyclrBottomBar(navController = navController)
            }
        }

        assertBottomBarTabsAreVisible()
        composeTestRule.onNodeWithContentDescription("Scan").assertIsDisplayed()
    }

    @Test
    fun bottomBar_darkMode_showsAllTabsAndScanAction() {
        composeTestRule.setContent {
            val navController = rememberTestBottomBarNavController()
            RecyclrTestHarness(isDark = true) {
                RecyclrBottomBar(navController = navController)
            }
        }

        assertBottomBarTabsAreVisible()
        composeTestRule.onNodeWithContentDescription("Scan").assertIsDisplayed()
    }

    @Test
    fun bottomBar_tappingRewards_navigatesToShopRoute() {
        lateinit var navController: TestNavHostController

        composeTestRule.setContent {
            navController = rememberTestBottomBarNavController()
            RecyclrTestHarness(isDark = false) {
                RecyclrBottomBar(navController = navController)
            }
        }

        composeTestRule.onNodeWithText("Rewards").performClick()
        composeTestRule.waitForIdle()

        assertEquals(BottomBarPage.Shop.route, navController.currentDestination?.route)
    }

    @Test
    fun bottomBar_tappingScan_navigatesToScanRoute() {
        lateinit var navController: TestNavHostController

        composeTestRule.setContent {
            navController = rememberTestBottomBarNavController()
            RecyclrTestHarness(isDark = true) {
                RecyclrBottomBar(navController = navController)
            }
        }

        composeTestRule.onNodeWithContentDescription("Scan").performClick()
        composeTestRule.waitForIdle()

        assertEquals(BottomBarPage.Scan.route, navController.currentDestination?.route)
    }

    private fun assertBottomBarTabsAreVisible() {
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rewards").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tips").assertIsDisplayed()
        composeTestRule.onNodeWithText("Profile").assertIsDisplayed()
    }
}

@Composable
private fun rememberTestBottomBarNavController(): TestNavHostController {
    val context = LocalContext.current
    return remember {
        TestNavHostController(context).apply {
            navigatorProvider.addNavigator(ComposeNavigator())
            graph = createGraph(startDestination = BottomBarPage.Home.route) {
                composable(BottomBarPage.Home.route) {}
                composable(BottomBarPage.Shop.route) {}
                composable(BottomBarPage.Scan.route) {}
                composable(BottomBarPage.Map.route) {}
                composable(BottomBarPage.Profile.route) {}
            }
        }
    }
}
