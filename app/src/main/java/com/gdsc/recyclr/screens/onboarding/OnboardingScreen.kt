package com.gdsc.recyclr.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.design.OnboardingPageIndicator
import com.gdsc.recyclr.components.design.RecyclrCategoryRow
import com.gdsc.recyclr.components.design.WaveBand
import com.gdsc.recyclr.ui.theme.RecyclrThemeColors
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
) {
    val pages = onboardingPages()
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val cfg = LocalConfiguration.current
    val horizontalPadding = if (cfg.screenWidthDp < 360) 12.dp else 20.dp
    val verticalPadding = if (cfg.screenHeightDp < 640) 16.dp else 28.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { page ->
                OnboardingPageContent(page = pages[page])
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OnboardingPageIndicator(
                    pageCount = pages.size,
                    currentPage = pagerState.currentPage,
                )
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = onFinish,
                ) {
                    Text(
                        text = stringResource(R.string.onboarding_skip),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                
                FloatingActionButton(
                    onClick = {
                        if (pagerState.currentPage == pages.lastIndex) {
                            onFinish()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    contentColor = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next",
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    val cfg = LocalConfiguration.current
    val cardPadH = if (cfg.screenWidthDp < 360) 16.dp else 24.dp
    val cardPadV = if (cfg.screenHeightDp < 640) 16.dp else 24.dp
    val compactH = cfg.screenHeightDp < 700
    // Bande vague plus basse et bornée : évite une « carte » visuelle trop haute sur grands écrans
    val waveHeight = max(96.dp, min(176.dp, (cfg.screenHeightDp * 0.19f).dp))
    val titleStyle =
        if (compactH) MaterialTheme.typography.headlineSmall
        else MaterialTheme.typography.headlineMedium

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = cardPadH, vertical = cardPadV),
        ) {
            Text(
                text = page.title,
                style = titleStyle,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(if (compactH) 8.dp else 12.dp))
            Text(
                text = page.subtitle,
                style = if (compactH) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(if (compactH) 12.dp else 16.dp))
            // Bloc illustration dimensionné au contenu, collé sous le texte ; l’espace libre reste en bas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentAlignment = Alignment.TopCenter,
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    WaveBand(
                        modifier = Modifier.align(Alignment.TopCenter),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        height = waveHeight,
                    )
                    when (page.kind) {
                        OnboardingPageKind.Intro -> IntroIllustration(
                            modifier = Modifier.align(Alignment.TopCenter),
                            compact = compactH,
                        )
                        OnboardingPageKind.Scan -> ScanPreview(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(horizontal = 4.dp, vertical = 8.dp),
                        )
                        OnboardingPageKind.Rewards -> RewardPreview(
                            modifier = Modifier.align(Alignment.TopCenter),
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun IntroIllustration(modifier: Modifier = Modifier, compact: Boolean = false) {
    val gap = if (compact) 10.dp else 14.dp
    val padH = if (compact) 8.dp else 12.dp
    Column(
        modifier = modifier.padding(horizontal = padH),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(gap),
    ) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 12.dp),
        ) {
            BinChip(label = "PLASTIC", color = Color(0xFF81C784), compact = compact)
            BinChip(label = "GLASS", color = Color(0xFF66BB6A), compact = compact)
            BinChip(label = "METAL", color = Color(0xFFFFD54F), compact = compact)
            BinChip(label = "PAPER", color = Color(0xFF64B5F6), compact = compact)
        }
        Text(
            text = stringResource(R.string.onboarding_intro_body),
            textAlign = TextAlign.Center,
            style = if (compact) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun BinChip(label: String, color: Color, compact: Boolean = false) {
    val w = if (compact) 44.dp else 50.dp
    val h = if (compact) 52.dp else 58.dp
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(if (compact) 2.dp else 4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(width = w, height = h)
                .clip(RoundedCornerShape(8.dp))
                .background(color),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun ScanPreview(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        RecyclrCategoryRow(
            title = stringResource(R.string.category_plastic),
            subtitle = stringResource(R.string.category_plastic_subtitle),
            iconBackground = RecyclrThemeColors.categoryPlastic,
            iconColor = RecyclrThemeColors.categoryPlasticIcon,
            icon = Icons.Default.LocalDrink,
            onClick = {},
        )
        RecyclrCategoryRow(
            title = stringResource(R.string.category_paper),
            subtitle = stringResource(R.string.category_paper_subtitle),
            iconBackground = RecyclrThemeColors.categoryPaper,
            iconColor = RecyclrThemeColors.categoryPaperIcon,
            icon = Icons.Default.Newspaper,
            onClick = {},
        )
    }
}

@Composable
private fun RewardPreview(modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.reward),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(64.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.onboarding_reward_earned),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "106", 
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.onboarding_points),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.onboarding_points_last_week), 
                style = MaterialTheme.typography.bodySmall, 
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
