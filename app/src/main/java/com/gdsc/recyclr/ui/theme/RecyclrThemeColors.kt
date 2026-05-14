package com.gdsc.recyclr.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.gdsc.recyclr.ui.preferences.LocalAppAppearance

object RecyclrThemeColors {
  private val isAppLightTheme: Boolean
    @Composable get() = !LocalAppAppearance.current.isDarkTheme

  val headerBackground: Color
    @Composable get() = if (isAppLightTheme) PointsCardGreen else Color(0xFF243B32)

  val pointsCard: Color
    @Composable get() = if (isAppLightTheme) PointsCardGreen else Color(0xFF2E4A3D)

  val scanCard: Color
    @Composable get() = if (isAppLightTheme) ScanCardPink else Color(0xFF4A3340)

  val categoryPlastic: Color
    @Composable get() = if (isAppLightTheme) CategoryPlastic else Color(0xFF355246)

  val categoryPaper: Color
    @Composable get() = if (isAppLightTheme) CategoryPaper else Color(0xFF2F4458)

  val categoryGlass: Color
    @Composable get() = if (isAppLightTheme) CategoryGlass else Color(0xFF4A3D34)

  val categoryMetal: Color
    @Composable get() = if (isAppLightTheme) CategoryMetal else Color(0xFF4A3340)

  val categoryTextile: Color
    @Composable get() = if (isAppLightTheme) CategoryTextile else Color(0xFF4A4634)

  val categoryPlasticIcon: Color
    @Composable get() = if (isAppLightTheme) CategoryPlasticDark else Color(0xFF81C784)

  val categoryPaperIcon: Color
    @Composable get() = if (isAppLightTheme) CategoryPaperDark else Color(0xFF64B5F6)

  val categoryGlassIcon: Color
    @Composable get() = if (isAppLightTheme) CategoryGlassDark else Color(0xFFFFB74D)

  val categoryMetalIcon: Color
    @Composable get() = if (isAppLightTheme) CategoryMetalDark else Color(0xFFF06292)

  val categoryTextileIcon: Color
    @Composable get() = if (isAppLightTheme) CategoryTextileDark else Color(0xFFFFF176)

  val onboardingCard: Color
    @Composable get() = if (isAppLightTheme) MintBackground else Color(0xFF2A3D33)

  val onboardingWave: Color
    @Composable get() = if (isAppLightTheme) SageGreen else Color(0xFF5F8A67)

  val onboardingBackdrop: Color
    @Composable get() = if (isAppLightTheme) ForestGreen else Color(0xFF142820)
}

@Composable
fun recyclrScreenBackground(): Color = MaterialTheme.colorScheme.background

@Composable
fun recyclrCardBackground(): Color = MaterialTheme.colorScheme.surface

@Composable
fun recyclrMutedText(): Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)

@Composable
fun isRecyclrDarkTheme(): Boolean = LocalAppAppearance.current.isDarkTheme
