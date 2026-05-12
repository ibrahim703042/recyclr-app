package com.gdsc.recyclr.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object RecyclrThemeColors {
  val headerBackground: Color
    @Composable get() = if (!isSystemInDarkTheme()) PointsCardGreen else Color(0xFF243B32)

  val pointsCard: Color
    @Composable get() = if (!isSystemInDarkTheme()) PointsCardGreen else Color(0xFF2E4A3D)

  val scanCard: Color
    @Composable get() = if (!isSystemInDarkTheme()) ScanCardPink else Color(0xFF4A3340)

  val categoryPlastic: Color
    @Composable get() = if (!isSystemInDarkTheme()) CategoryPlastic else Color(0xFF355246)

  val categoryPaper: Color
    @Composable get() = if (!isSystemInDarkTheme()) CategoryPaper else Color(0xFF2F4458)

  val categoryGlass: Color
    @Composable get() = if (!isSystemInDarkTheme()) CategoryGlass else Color(0xFF4A3D34)

  val categoryMetal: Color
    @Composable get() = if (!isSystemInDarkTheme()) CategoryMetal else Color(0xFF4A3340)

  val categoryTextile: Color
    @Composable get() = if (!isSystemInDarkTheme()) CategoryTextile else Color(0xFF4A4634)

  val categoryPlasticIcon: Color
    @Composable get() = if (!isSystemInDarkTheme()) CategoryPlasticDark else Color(0xFF81C784)

  val categoryPaperIcon: Color
    @Composable get() = if (!isSystemInDarkTheme()) CategoryPaperDark else Color(0xFF64B5F6)

  val categoryGlassIcon: Color
    @Composable get() = if (!isSystemInDarkTheme()) CategoryGlassDark else Color(0xFFFFB74D)

  val categoryMetalIcon: Color
    @Composable get() = if (!isSystemInDarkTheme()) CategoryMetalDark else Color(0xFFF06292)

  val categoryTextileIcon: Color
    @Composable get() = if (!isSystemInDarkTheme()) CategoryTextileDark else Color(0xFFFFF176)

  val onboardingCard: Color
    @Composable get() = if (!isSystemInDarkTheme()) MintBackground else Color(0xFF2A3D33)

  val onboardingWave: Color
    @Composable get() = if (!isSystemInDarkTheme()) SageGreen else Color(0xFF5F8A67)

  val onboardingBackdrop: Color
    @Composable get() = if (!isSystemInDarkTheme()) ForestGreen else Color(0xFF142820)
}

@Composable
fun recyclrScreenBackground(): Color = MaterialTheme.colorScheme.background

@Composable
fun recyclrCardBackground(): Color = MaterialTheme.colorScheme.surface

@Composable
fun recyclrMutedText(): Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)

@Composable
fun isRecyclrDarkTheme(): Boolean = isSystemInDarkTheme()
