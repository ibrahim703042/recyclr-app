package com.gdsc.recyclr.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object RecyclrThemeColors {
  val headerBackground: Color
    @Composable get() = if (MaterialTheme.colors.isLight) PointsCardGreen else Color(0xFF243B32)

  val pointsCard: Color
    @Composable get() = if (MaterialTheme.colors.isLight) PointsCardGreen else Color(0xFF2E4A3D)

  val scanCard: Color
    @Composable get() = if (MaterialTheme.colors.isLight) ScanCardPink else Color(0xFF4A3340)

  val categoryPlastic: Color
    @Composable get() = if (MaterialTheme.colors.isLight) CategoryPlastic else Color(0xFF355246)

  val categoryPaper: Color
    @Composable get() = if (MaterialTheme.colors.isLight) CategoryPaper else Color(0xFF2F4458)

  val categoryGlass: Color
    @Composable get() = if (MaterialTheme.colors.isLight) CategoryGlass else Color(0xFF4A3D34)

  val categoryMetal: Color
    @Composable get() = if (MaterialTheme.colors.isLight) CategoryMetal else Color(0xFF4A3340)

  val categoryTextile: Color
    @Composable get() = if (MaterialTheme.colors.isLight) CategoryTextile else Color(0xFF4A4634)

  val onboardingCard: Color
    @Composable get() = if (MaterialTheme.colors.isLight) MintBackground else Color(0xFF2A3D33)

  val onboardingWave: Color
    @Composable get() = if (MaterialTheme.colors.isLight) SageGreen else Color(0xFF5F8A67)

  val onboardingBackdrop: Color
    @Composable get() = if (MaterialTheme.colors.isLight) ForestGreen else Color(0xFF142820)
}

@Composable
fun recyclrScreenBackground(): Color = MaterialTheme.colors.background

@Composable
fun recyclrCardBackground(): Color = MaterialTheme.colors.surface

@Composable
fun recyclrMutedText(): Color = MaterialTheme.colors.onBackground.copy(alpha = 0.7f)

@Composable
fun isRecyclrDarkTheme(): Boolean = !MaterialTheme.colors.isLight || isSystemInDarkTheme()
