package com.gdsc.recyclr.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.design.OnboardingPageIndicator
import com.gdsc.recyclr.components.preferences.ThemeToggleIconButton
import com.gdsc.recyclr.components.design.RecyclrCategoryRow
import com.gdsc.recyclr.components.design.WaveBand
import com.gdsc.recyclr.ui.theme.LeafGreen
import com.gdsc.recyclr.ui.theme.RecyclrThemeColors
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val kind: OnboardingPageKind,
)

@Composable
private fun onboardingPages(): List<OnboardingPage> = listOf(
    OnboardingPage(
        title = stringResource(R.string.onboarding_title_intro),
        subtitle = stringResource(R.string.onboarding_subtitle),
        kind = OnboardingPageKind.Intro,
    ),
    OnboardingPage(
        title = stringResource(R.string.onboarding_title_scan),
        subtitle = stringResource(R.string.onboarding_subtitle),
        kind = OnboardingPageKind.Scan,
    ),
    OnboardingPage(
        title = stringResource(R.string.onboarding_title_rewards),
        subtitle = stringResource(R.string.onboarding_subtitle),
        kind = OnboardingPageKind.Rewards,
    ),
)

private enum class OnboardingPageKind { Intro, Scan, Rewards }

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val pages = onboardingPages()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RecyclrThemeColors.onboardingBackdrop),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 28.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                ThemeToggleIconButton()
            }
            HorizontalPager(
                count = pages.size,
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { page ->
                OnboardingPageContent(page = pages[page])
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OnboardingPageIndicator(
                    pageCount = pages.size,
                    currentPage = pagerState.currentPage,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.onboarding_skip),
                    color = Color.White,
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickableNoRipple { onFinish() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                )
                IconButton(
                    onClick = {
                        if (pagerState.currentPage == pages.lastIndex) {
                            onFinish()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(LeafGreen),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next",
                        tint = Color.White,
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(32.dp),
        backgroundColor = RecyclrThemeColors.onboardingCard,
        elevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 28.dp),
        ) {
            Text(
                text = page.title,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                lineHeight = 28.sp,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = page.subtitle,
                fontSize = 14.sp,
                color = Color(0xFF4A4A4A),
                lineHeight = 20.sp,
            )
            Spacer(modifier = Modifier.height(18.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                WaveBand(
                    modifier = Modifier.align(Alignment.Center),
                    color = RecyclrThemeColors.onboardingWave,
                    height = 220.dp,
                )
                when (page.kind) {
                    OnboardingPageKind.Intro -> IntroIllustration(
                        modifier = Modifier.align(Alignment.Center),
                    )
                    OnboardingPageKind.Scan -> ScanPreview(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 8.dp),
                    )
                    OnboardingPageKind.Rewards -> RewardPreview(
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
        }
    }
}

@Composable
private fun IntroIllustration(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BinChip(label = "PLASTIC", color = Color(0xFFE57373))
            BinChip(label = "GLASS", color = Color(0xFF66BB6A))
            BinChip(label = "METAL", color = Color(0xFFFFD54F))
            BinChip(label = "PAPER", color = Color(0xFF64B5F6))
        }
        Text(
            text = stringResource(R.string.onboarding_intro_body),
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            color = Color(0xFF2F4F3F),
        )
    }
}

@Composable
private fun BinChip(label: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(width = 54.dp, height = 64.dp)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomEnd = 4.dp, bottomStart = 4.dp))
                .background(color),
        )
        Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ScanPreview(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        RecyclrCategoryRow(
            title = stringResource(R.string.category_plastic),
            subtitle = stringResource(R.string.category_plastic_subtitle),
            iconBackground = RecyclrThemeColors.categoryPlastic,
            icon = Icons.Default.LocalDrink,
            onClick = {},
        )
        RecyclrCategoryRow(
            title = stringResource(R.string.category_paper),
            subtitle = stringResource(R.string.category_paper_subtitle),
            iconBackground = RecyclrThemeColors.categoryPaper,
            icon = Icons.Default.Newspaper,
            onClick = {},
        )
        RecyclrCategoryRow(
            title = stringResource(R.string.category_glass),
            subtitle = stringResource(R.string.category_glass_subtitle),
            iconBackground = RecyclrThemeColors.categoryGlass,
            icon = Icons.Default.WineBar,
            onClick = {},
        )
    }
}

@Composable
private fun RewardPreview(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        shape = RoundedCornerShape(24.dp),
        backgroundColor = androidx.compose.material.MaterialTheme.colors.surface,
        elevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(RecyclrThemeColors.pointsCard, RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.reward),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(56.dp),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = stringResource(R.string.onboarding_reward_earned), fontSize = 14.sp)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = "106", fontWeight = FontWeight.Bold, fontSize = 34.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.onboarding_points),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = stringResource(R.string.onboarding_points_last_week), fontSize = 12.sp, color = Color(0xFF5A5A5A))
        }
    }
}

private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier =
    this.then(
        Modifier.clickable(
            indication = null,
            interactionSource = MutableInteractionSource(),
            onClick = onClick,
        ),
    )
