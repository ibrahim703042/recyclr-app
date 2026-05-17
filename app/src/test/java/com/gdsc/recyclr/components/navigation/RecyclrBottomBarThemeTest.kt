package com.gdsc.recyclr.components.navigation

import com.gdsc.recyclr.navigation.BottomBarPage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecyclrBottomBarThemeTest {

    @Test
    fun lightAndDarkPalettes_useDifferentBarBackgrounds() {
        val light = bottomBarThemeColors(isDark = false)
        val dark = bottomBarThemeColors(isDark = true)

        assertNotEquals(light.barBackground, dark.barBackground)
        assertNotEquals(light.activeTint, dark.activeTint)
        assertNotEquals(light.inactiveTint, dark.inactiveTint)
        assertNotEquals(light.fabBorder, dark.fabBorder)
    }

    @Test
    fun lightPalette_usesWhiteNavBar() {
        val light = bottomBarThemeColors(isDark = false)

        assertEquals(1f, light.barBackground.red, 0.001f)
        assertEquals(1f, light.barBackground.green, 0.001f)
        assertEquals(1f, light.barBackground.blue, 0.001f)
        assertTrue(light.barShadowElevation.value > 0f)
        assertTrue(light.fabShadowElevation.value > light.barShadowElevation.value)
    }

    @Test
    fun darkPalette_usesElevatedFabWithoutBarShadow() {
        val dark = bottomBarThemeColors(isDark = true)

        assertEquals(0f, dark.barShadowElevation.value)
        assertTrue(dark.fabShadowElevation.value > 0f)
        assertTrue(dark.barBackground.red < 0.2f)
        assertTrue(dark.barBackground.green < 0.2f)
        assertTrue(dark.barBackground.blue < 0.2f)
    }

    @Test
    fun sideTabRoutes_containsFourTabsInDisplayOrder() {
        assertEquals(
            listOf("home", "shop", "map", "profile"),
            RecyclrBottomBarDefaults.sideTabRoutes,
        )
    }

    @Test
    fun isTabSelected_matchesRouteInHierarchy() {
        val hierarchy = listOf("main", BottomBarPage.Shop.route)

        assertTrue(
            RecyclrBottomBarDefaults.isTabSelected(BottomBarPage.Shop.route, hierarchy),
        )
        assertFalse(
            RecyclrBottomBarDefaults.isTabSelected(BottomBarPage.Home.route, hierarchy),
        )
    }

    @Test
    fun isScanSelected_matchesScanRouteOnly() {
        val onScan = listOf("main", BottomBarPage.Scan.route)
        val onHome = listOf("main", BottomBarPage.Home.route)

        assertTrue(RecyclrBottomBarDefaults.isScanSelected(onScan))
        assertFalse(RecyclrBottomBarDefaults.isScanSelected(onHome))
    }
}
