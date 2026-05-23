package com.gdsc.recyclr.screens.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// ── Brand tokens ─────────────────────────────────────────────────────────────
private val BrandTeal       = Color(0xFF1D9E75)
private val BrandTealLight  = Color(0xFFE1F5EE)
private val BrandTealMid    = Color(0xFFC8EDE0)
private val BrandTealDark   = Color(0xFF0F6E56)
private val BrandTealDeep   = Color(0xFF085041)

// ── Page model ───────────────────────────────────────────────────────────────
private sealed interface OnboardingKind {
    data object Intro   : OnboardingKind
    data object Scan    : OnboardingKind
    data object Rewards : OnboardingKind
}

private data class OnboardingPage(
    val tag:      String,
    val title:    String,
    val subtitle: String,
    val kind:     OnboardingKind,
)

private val pages = listOf(
    OnboardingPage(
        tag      = "Welcome to Recyclr",
        title    = "Recycling made simple & rewarding",
        subtitle = "Scan any item, instantly know how to recycle it, and earn points for every action you take for the planet.",
        kind     = OnboardingKind.Intro,
    ),
    OnboardingPage(
        tag      = "Smart scanning",
        title    = "Point. Scan. Know instantly.",
        subtitle = "Our AI identifies any item in seconds and tells you exactly which bin it belongs in — no guessing required.",
        kind     = OnboardingKind.Scan,
    ),
    OnboardingPage(
        tag      = "Earn rewards",
        title    = "Every item recycled earns you points",
        subtitle = "Climb the leaderboard, unlock badges, and redeem your points for real rewards. Good for you, great for the planet.",
        kind     = OnboardingKind.Rewards,
    ),
)

// ── Screen ───────────────────────────────────────────────────────────────────
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope      = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandTeal),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
        ) {
            // ── Top Header (Logo + Skip) ──────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mini Logo
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = com.gdsc.recyclr.R.drawable.recycle),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Recyclr",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Skip button
                if (pagerState.currentPage != pages.lastIndex) {
                    TextButton(onClick = onFinish) {
                        Text(
                            text = "Skip",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            // ── Pager ─────────────────────────────────────────────────────
            HorizontalPager(
                state    = pagerState,
                modifier = Modifier.weight(1f),
            ) { index ->
                PageCard(page = pages[index])
            }

            // ── Bottom bar ────────────────────────────────────────────────
            BottomBar(
                pagerState = pagerState,
                onNext     = {
                    if (pagerState.currentPage == pages.lastIndex) {
                        onFinish()
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
            )
        }
    }
}

// ── Page card (white rounded card with wave + illustration + text) ────────────
@Composable
private fun PageCard(page: OnboardingPage) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        shape    = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        color    = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Wave area with illustration on top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
            ) {
                // Back wave (lighter)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(
                            RoundedCornerShape(
                                bottomStart = 64.dp,
                                bottomEnd   = 64.dp,
                            )
                        )
                        .background(BrandTealMid),
                )
                // Front wave (brand light)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .align(Alignment.TopCenter)
                        .clip(
                            RoundedCornerShape(
                                bottomStart = 80.dp,
                                bottomEnd   = 80.dp,
                            )
                        )
                        .background(BrandTealLight),
                )
                // Illustration
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    when (page.kind) {
                        OnboardingKind.Intro   -> IntroIllustration()
                        OnboardingKind.Scan    -> ScanIllustration()
                        OnboardingKind.Rewards -> RewardsIllustration()
                    }
                }
            }

            // Text content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 24.dp),
            ) {
                // Tag line
                Text(
                    text  = page.tag.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.5.sp,
                        fontWeight    = FontWeight.ExtraBold,
                    ),
                    color = BrandTeal,
                )
                Spacer(Modifier.height(12.dp))
                // Title
                Text(
                    text       = page.title,
                    style      = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 32.sp,
                )
                Spacer(Modifier.height(16.dp))
                // Subtitle
                Text(
                    text  = page.subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 26.sp,
                )
            }
        }
    }
}

// ── Bottom bar ────────────────────────────────────────────────────────────────
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun BottomBar(
    pagerState : PagerState,
    onNext     : () -> Unit,
) {
    val isLast = pagerState.currentPage == pages.lastIndex

    Surface(
        color          = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Dot indicators
            Row(
                modifier             = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment    = Alignment.CenterVertically,
            ) {
                repeat(pages.size) { i ->
                    PagerDot(active = i == pagerState.currentPage)
                }
            }

            // Next / Done FAB
            FloatingActionButton(
                onClick        = onNext,
                containerColor = BrandTeal,
                contentColor   = Color.White,
                shape          = CircleShape,
                modifier       = Modifier.size(56.dp),
                elevation      = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 4.dp,
                ),
            ) {
                if (isLast) {
                    Icon(Icons.Default.Check, contentDescription = "Get started")
                } else {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next")
                }
            }
        }
    }
}

// Animated dot — stretches into a pill when active
@Composable
private fun PagerDot(active: Boolean) {
    val width by animateDpAsState(
        targetValue = if (active) 28.dp else 8.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "dot_width",
    )
    val color by animateColorAsState(
        targetValue = if (active) BrandTeal else BrandTealMid,
        animationSpec = tween(300),
        label = "dot_color",
    )
    Box(
        modifier = Modifier
            .height(8.dp)
            .width(width)
            .clip(CircleShape)
            .background(color),
    )
}

// ── Illustrations ─────────────────────────────────────────────────────────────

// Page 1 — coloured bin chips + category tags
@Composable
private fun IntroIllustration() {
    val bins = listOf(
        "PLASTIC" to Color(0xFF81C784),
        "GLASS"   to Color(0xFF66BB6A),
        "METAL"   to Color(0xFFFFD54F),
        "PAPER"   to Color(0xFF64B5F6),
    )
    val tags = listOf(
        "Plastic" to Color(0xFFE8F5E9) to Color(0xFF2E7D32),
        "Paper"   to Color(0xFFE3F2FD) to Color(0xFF0D47A1),
        "Metal"   to Color(0xFFFFFDE7) to Color(0xFFF57F17),
        "Glass"   to Color(0xFFF3E5F5) to Color(0xFF6A1B9A),
    )

    Column(
        horizontalAlignment  = Alignment.CenterHorizontally,
        verticalArrangement  = Arrangement.spacedBy(16.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            bins.forEach { (label, color) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 48.dp, height = 58.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(color),
                    )
                    Text(
                        text       = label,
                        style      = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            tags.forEach { (pair, textColor) ->
                val (label, bgColor) = pair
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(bgColor)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                    Text(
                        text       = label,
                        style      = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color      = textColor,
                    )
                }
            }
        }
    }
}

// Page 2 — fake scanner frame with two category rows
@Composable
private fun ScanIllustration() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(
                width = 2.dp,
                color = BrandTeal,
                shape = RoundedCornerShape(16.dp),
            )
            .padding(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ScanCategoryRow(
                label    = "Plastic bottle",
                subtitle = "Rinse & place in blue bin",
                iconBg   = Color(0xFFE8F5E9),
                iconTint = Color(0xFF2E7D32),
                icon     = Icons.Default.LocalDrink,
            )
            ScanCategoryRow(
                label    = "Newspaper",
                subtitle = "Flatten & place in yellow bin",
                iconBg   = Color(0xFFFFF8E1),
                iconTint = Color(0xFFF57F17),
                icon     = Icons.Default.Newspaper,
            )
        }
    }
}

@Composable
private fun ScanCategoryRow(
    label:    String,
    subtitle: String,
    iconBg:   Color,
    iconTint: Color,
    icon:     ImageVector,
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = BrandTealLight,
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier          = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier         = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(label,    style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = BrandTealMid, modifier = Modifier.size(16.dp))
        }
    }
}

// Page 3 — reward card (same bordered white card as ScanIllustration)
@Composable
private fun RewardsIllustration() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(
                width = 2.dp,
                color = BrandTeal,
                shape = RoundedCornerShape(16.dp),
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier         = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(BrandTealLight),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint               = BrandTeal,
                    modifier           = Modifier.size(36.dp),
                )
            }
            Text(
                text       = "POINTS EARNED",
                style      = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                fontWeight = FontWeight.Bold,
                color      = BrandTealDark,
            )
            Row(
                verticalAlignment     = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text       = "106",
                    style      = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                    color      = BrandTealDeep,
                )
                Text(
                    text     = "pts",
                    style    = MaterialTheme.typography.titleMedium,
                    color    = BrandTeal,
                    modifier = Modifier.padding(bottom = 6.dp),
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RewardBadge(label = "+12 today", bg = BrandTealLight, fg = BrandTealDark)
                RewardBadge(label = "Level 3",   bg = BrandTealDark,  fg = BrandTealLight)
            }
            Text(
                text      = "Earned last week recycling 14 items",
                style     = MaterialTheme.typography.labelSmall,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun RewardBadge(label: String, bg: Color, fg: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 5.dp),
    ) {
        Text(
            text       = label,
            style      = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color      = fg,
        )
    }
}
