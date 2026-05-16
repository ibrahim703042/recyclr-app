@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.gdsc.recyclr.components.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.gdsc.recyclr.R
import com.gdsc.recyclr.data.service.LiveActivity
import com.gdsc.recyclr.domain.model.engagement.*
import com.gdsc.recyclr.ui.theme.GreenHero
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

// ─────────────────────────────────────────────────────────────────────────────
// Palette
// ─────────────────────────────────────────────────────────────────────────────

data class HomeRedesignPalette(
    val primary: Color,
    val onPrimary: Color,
    val pageBackground: Color,
    val card: Color,
    val onCard: Color,
    val muted: Color,
    val greeting: Color,
    val divider: Color,
    val highlightRow: Color,
    val softAccent: Color,
    val streakBanner: Color,
    val progressTrack: Color,
    val chipStroke: Color,
    val chipSurface: Color,
    val walletSurface: Color,
    val pointsStar: Color,
    val headerBackground: Color,
)

private fun ColorScheme.isDark(): Boolean = background.luminance() < 0.5f

private fun buildHomePalette(cs: ColorScheme): HomeRedesignPalette {
    val dark = cs.isDark()
    if (!dark) {
        return HomeRedesignPalette(
            primary          = GreenHero.Primary,
            onPrimary        = GreenHero.OnPrimary,
            pageBackground   = GreenHero.PageBackground,
            card             = GreenHero.Card,
            onCard           = GreenHero.TextPrimary,
            muted            = GreenHero.TextSecondary,
            greeting         = GreenHero.TextSecondary,
            divider          = GreenHero.Divider,
            highlightRow     = GreenHero.LightGreen,
            softAccent       = GreenHero.LightGreen,
            streakBanner     = GreenHero.StreakBackground,
            progressTrack    = GreenHero.LightGreenMuted,
            chipStroke       = GreenHero.ChipBorder,
            chipSurface      = GreenHero.Card,
            walletSurface    = GreenHero.LightGreen,
            pointsStar       = GreenHero.PointsStar,
            headerBackground = GreenHero.Primary,
        )
    }
    return HomeRedesignPalette(
        primary          = cs.primary,
        onPrimary        = cs.onPrimary,
        pageBackground   = cs.background,
        card             = cs.surface,
        onCard           = cs.onSurface,
        muted            = cs.onSurfaceVariant,
        greeting         = cs.onSurfaceVariant,
        divider          = cs.outline.copy(alpha = 0.28f),
        highlightRow     = cs.primaryContainer.copy(alpha = 0.4f),
        softAccent       = cs.primaryContainer.copy(alpha = 0.5f),
        streakBanner     = Color(0xFF2F2818),
        progressTrack    = cs.surfaceVariant,
        chipStroke       = cs.outline.copy(alpha = 0.25f),
        chipSurface      = cs.surface,
        walletSurface    = cs.primaryContainer.copy(alpha = 0.45f),
        pointsStar       = GreenHero.PointsStar,
        headerBackground = cs.primary,
    )
}

val LocalHomeRedesignPalette = compositionLocalOf<HomeRedesignPalette> {
    error("Provide HomeRedesignThemeProvider")
}

@Composable
fun rememberHomeRedesignPalette(): HomeRedesignPalette {
    val cs = MaterialTheme.colorScheme
    return remember(cs) { buildHomePalette(cs) }
}

@Composable
fun HomeRedesignThemeProvider(content: @Composable () -> Unit) {
    val palette = rememberHomeRedesignPalette()
    CompositionLocalProvider(LocalHomeRedesignPalette provides palette, content = content)
}

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun HomeSectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text       = text,
        style      = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color      = LocalHomeRedesignPalette.current.onCard,
        modifier   = modifier,
    )
}

@Composable
fun formatRelativePostTime(createdAtMillis: Long?): String {
    val millis = createdAtMillis ?: return stringResource(R.string.home_time_recent)
    val diff   = (System.currentTimeMillis() - millis).coerceAtLeast(0L)
    if (diff < 60_000L) return stringResource(R.string.home_time_just_now)
    val mins  = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days  = TimeUnit.MILLISECONDS.toDays(diff)
    return when {
        mins  < 60 -> stringResource(R.string.home_time_mins_ago,  mins.coerceAtLeast(1))
        hours < 24 -> stringResource(R.string.home_time_hours_ago, hours)
        else       -> stringResource(R.string.home_time_days_ago,  days.coerceAtLeast(1))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Greeting header  — full-width teal banner
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun HomeGreetingHeader(
    userName: String,
    photoUrl: String?,
    unreadNotificationCount: Int,
    liveActivity: LiveActivity? = null,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val headerShape = RoundedCornerShape(
        bottomStart = GreenHero.CornerHeader,
        bottomEnd   = GreenHero.CornerHeader,
    )
    Surface(
        color          = LocalHomeRedesignPalette.current.headerBackground,
        shape          = headerShape,
        tonalElevation = 0.dp,
        modifier       = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(bottom = 16.dp),
        ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Avatar / profile photo
            IconButton(onClick = onProfileClick, modifier = Modifier.size(44.dp)) {
                Box(
                    modifier         = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.22f)),
                    contentAlignment = Alignment.Center,
                ) {
                    if (!photoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model        = photoUrl,
                            contentDescription = null,
                            modifier     = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Icon(
                            Icons.Outlined.EnergySavingsLeaf,
                            contentDescription = null,
                            tint               = Color.White,
                            modifier           = Modifier.size(22.dp),
                        )
                    }
                }
            }

            // Name
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text  = stringResource(R.string.home_welcome_back_comma),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.78f),
                )
                Text(
                    text       = userName,
                    style      = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White,
                )
            }

            // Notification bell
            IconButton(onClick = onNotificationClick, modifier = Modifier.size(44.dp)) {
                Box(
                    modifier         = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center,
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadNotificationCount > 0) {
                                Badge(containerColor = MaterialTheme.colorScheme.error) {
                                    Text(
                                        text  = if (unreadNotificationCount > 9) "9+" else unreadNotificationCount.toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector        = Icons.Outlined.Notifications,
                            contentDescription = null,
                            tint               = Color.White,
                            modifier           = Modifier.size(22.dp),
                        )
                    }
                }
            }
        }
            liveActivity?.let { activity ->
                HomeLiveTicker(
                    activity = activity,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 4.dp),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Live activity ticker
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun HomeLiveTicker(
    activity: LiveActivity,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "live_pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue   = 1f,
        targetValue    = 0.3f,
        animationSpec  = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label          = "pulse",
    )

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.92f),
        tonalElevation = 0.dp,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(LocalHomeRedesignPalette.current.primary.copy(alpha = alpha)),
            )
            Icon(
                Icons.Outlined.EnergySavingsLeaf,
                contentDescription = null,
                tint     = LocalHomeRedesignPalette.current.primary,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text  = "${activity.userName} just recycled ${activity.itemType.lowercase()}!",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = LocalHomeRedesignPalette.current.primary,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Collector shortcut banner
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun HomeCollectorShortcut(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick        = onClick,
        modifier       = modifier.fillMaxWidth(),
        shape          = RoundedCornerShape(14.dp),
        color          = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier          = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier         = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Active pickup jobs", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text("2 pending tasks", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(18.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Stats row — 3 cards
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun HomeStatsRow(
    co2Kg: Float,
    points: Int,
    trees: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier              = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        HomeStatCard(
            modifier = Modifier.weight(1f),
            iconContent = {
                Box(
                    modifier         = Modifier.size(32.dp).clip(CircleShape).background(LocalHomeRedesignPalette.current.softAccent),
                    contentAlignment = Alignment.Center,
                ) { Icon(Icons.Outlined.EnergySavingsLeaf, null, tint = LocalHomeRedesignPalette.current.primary, modifier = Modifier.size(18.dp)) }
            },
            value = String.format("%.1f kg", co2Kg),
            label = stringResource(R.string.home_stat_co2),
        )
        HomeStatCard(
            modifier = Modifier.weight(1f),
            iconContent = {
                Box(
                    modifier         = Modifier.size(32.dp).clip(CircleShape).background(GreenHero.PointsStarBg),
                    contentAlignment = Alignment.Center,
                ) { Icon(Icons.Default.Star, null, tint = LocalHomeRedesignPalette.current.pointsStar, modifier = Modifier.size(18.dp)) }
            },
            value = points.toString(),
            label = stringResource(R.string.home_stat_points),
        )
        HomeStatCard(
            modifier = Modifier.weight(1f),
            iconContent = {
                Box(
                    modifier         = Modifier.size(32.dp).clip(CircleShape).background(GreenHero.TreesIconBg),
                    contentAlignment = Alignment.Center,
                ) { Icon(Icons.Default.Park, null, tint = GreenHero.TreesIcon, modifier = Modifier.size(18.dp)) }
            },
            value = trees.toString(),
            label = stringResource(R.string.home_stat_trees),
        )
    }
}

@Composable
private fun HomeStatCard(
    iconContent: @Composable () -> Unit,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier       = modifier.shadow(2.dp, RoundedCornerShape(GreenHero.CornerCard)),
        shape          = RoundedCornerShape(GreenHero.CornerCard),
        color          = LocalHomeRedesignPalette.current.card,
        tonalElevation = 0.dp,
        border         = BorderStroke(0.5.dp, LocalHomeRedesignPalette.current.chipStroke),
    ) {
        Column(
            modifier              = Modifier.fillMaxWidth().padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment   = Alignment.CenterHorizontally,
            verticalArrangement   = Arrangement.spacedBy(6.dp),
        ) {
            iconContent()
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = LocalHomeRedesignPalette.current.onCard)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = LocalHomeRedesignPalette.current.muted, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Wallet card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun HomeWalletSummaryCard(
    wallet: RecWallet,
    onOpenWallet: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick        = onOpenWallet,
        modifier       = modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(GreenHero.CornerCard)),
        shape          = RoundedCornerShape(GreenHero.CornerCard),
        color          = LocalHomeRedesignPalette.current.walletSurface,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier         = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LocalHomeRedesignPalette.current.primary),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text  = stringResource(R.string.home_wallet_rec_title).uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
                    fontWeight = FontWeight.Bold,
                    color = LocalHomeRedesignPalette.current.primary,
                )
                Text(
                    text       = stringResource(R.string.home_wallet_rec_balance, wallet.recBalance),
                    style      = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color      = LocalHomeRedesignPalette.current.onCard,
                )
                Text(
                    text  = stringResource(R.string.home_wallet_co2_credits, wallet.carbonCreditsTonnes),
                    style = MaterialTheme.typography.labelSmall,
                    color = LocalHomeRedesignPalette.current.muted,
                )
            }
            Text(
                text       = stringResource(R.string.home_details_link),
                style      = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color      = LocalHomeRedesignPalette.current.primary,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Eco streak banner
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun HomeEcoStreakBanner(
    streak: EcoStreak,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape  = RoundedCornerShape(GreenHero.CornerCard),
        color  = LocalHomeRedesignPalette.current.streakBanner,
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, GreenHero.StreakBorder),
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = GreenHero.StreakIcon, modifier = Modifier.size(26.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = stringResource(R.string.home_eco_streak_inline, streak.currentDays),
                    style      = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color      = GreenHero.StreakText,
                )
                Text(
                    text  = stringResource(R.string.home_streak_multiplier, streak.multiplier),
                    style = MaterialTheme.typography.labelSmall,
                    color = GreenHero.StreakSubtext,
                )
            }
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = GreenHero.StreakBadge,
            ) {
                Text(
                    text       = "${streak.multiplier}×",
                    style      = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color      = GreenHero.StreakBadgeText,
                    modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Weekly challenge card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun HomeWeeklyChallengeCard(
    challenge: WeeklyChallenge,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val progress = (challenge.currentScans.toFloat() / challenge.targetScans.coerceAtLeast(1)).coerceIn(0f, 1f)
    Surface(
        onClick        = onClick,
        modifier       = modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(GreenHero.CornerCard)),
        shape          = RoundedCornerShape(GreenHero.CornerCard),
        color          = LocalHomeRedesignPalette.current.card,
        tonalElevation = 0.dp,
        border         = BorderStroke(0.5.dp, LocalHomeRedesignPalette.current.chipStroke),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Text(
                    text       = challenge.title,
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color      = LocalHomeRedesignPalette.current.onCard,
                    modifier   = Modifier.weight(1f),
                )
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = LocalHomeRedesignPalette.current.softAccent,
                ) {
                    Text(
                        text       = stringResource(R.string.home_challenge_pts, challenge.rewardPoints),
                        style      = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color      = LocalHomeRedesignPalette.current.primary,
                        modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text  = challenge.description,
                style = MaterialTheme.typography.bodySmall,
                color = LocalHomeRedesignPalette.current.muted,
            )
            Spacer(Modifier.height(14.dp))
            LinearProgressIndicator(
                progress    = { progress },
                modifier    = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color       = LocalHomeRedesignPalette.current.primary,
                trackColor  = LocalHomeRedesignPalette.current.progressTrack,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text  = stringResource(R.string.home_weekly_challenge_progress, challenge.currentScans, challenge.targetScans, challenge.endsInDays),
                style = MaterialTheme.typography.labelSmall,
                color = LocalHomeRedesignPalette.current.muted,
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Challenge + Leaderboard auto-pager
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun HomeChallengeLeaderboardCarousel(
    challenge: WeeklyChallenge,
    entries: List<LeaderboardEntry>,
    onOpenChallenge: () -> Unit,
    onOpenLeaderboard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { 2 })

    LaunchedEffect(pagerState) {
        while (true) {
            delay(5_000L)
            pagerState.animateScrollToPage((pagerState.settledPage + 1) % 2)
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth()) { page ->
            when (page) {
                0 -> HomeWeeklyChallengeCard(challenge = challenge, onClick = onOpenChallenge, modifier = Modifier.fillMaxWidth())
                else -> HomeLeaderboardPreviewCard(entries = entries, onOpenLeaderboard = onOpenLeaderboard, modifier = Modifier.fillMaxWidth())
            }
        }
        Spacer(Modifier.height(8.dp))
        // Page dots
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            repeat(2) { i ->
                val active = i == pagerState.currentPage
                val width  by animateDpAsState(if (active) 16.dp else 6.dp, spring(Spring.DampingRatioMediumBouncy), label = "dot$i")
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .height(6.dp)
                        .width(width)
                        .clip(CircleShape)
                        .background(if (active) LocalHomeRedesignPalette.current.primary else LocalHomeRedesignPalette.current.muted.copy(alpha = 0.35f))
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Leaderboard card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun HomeLeaderboardPreviewCard(
    entries: List<LeaderboardEntry>,
    onOpenLeaderboard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val rankTints = listOf(Color(0xFFFFB300), Color(0xFF9E9E9E), Color(0xFFCD7F32))

    Surface(
        modifier       = modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(GreenHero.CornerCard)),
        shape          = RoundedCornerShape(GreenHero.CornerCard),
        color          = LocalHomeRedesignPalette.current.card,
        tonalElevation = 0.dp,
        border         = BorderStroke(0.5.dp, LocalHomeRedesignPalette.current.chipStroke),
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.home_leaderboard), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = LocalHomeRedesignPalette.current.onCard)
                TextButton(onClick = onOpenLeaderboard) {
                    Text(stringResource(R.string.home_view_all), style = MaterialTheme.typography.labelMedium, color = LocalHomeRedesignPalette.current.primary)
                }
            }
            entries.take(3).forEachIndexed { index, entry ->
                val isYou = entry.isCurrentUser
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (isYou) Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(LocalHomeRedesignPalette.current.highlightRow)
                                .border(1.dp, LocalHomeRedesignPalette.current.primary.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                            else Modifier
                        )
                        .padding(vertical = 8.dp, horizontal = if (isYou) 8.dp else 0.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // Rank number with colour
                    Text(
                        text       = entry.rank.toString(),
                        style      = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color      = rankTints.getOrElse(index) { LocalHomeRedesignPalette.current.muted },
                        modifier   = Modifier.width(20.dp),
                    )
                    // Avatar initials
                    Box(
                        modifier         = Modifier.size(28.dp).clip(CircleShape).background(LocalHomeRedesignPalette.current.softAccent),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text  = entry.name.take(2).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = LocalHomeRedesignPalette.current.primary,
                        )
                    }
                    Text(
                        text       = if (isYou) stringResource(R.string.home_leaderboard_you) else entry.name,
                        style      = MaterialTheme.typography.bodySmall,
                        fontWeight = if (isYou) FontWeight.Bold else FontWeight.Normal,
                        color      = if (isYou) LocalHomeRedesignPalette.current.primary else LocalHomeRedesignPalette.current.onCard,
                        modifier   = Modifier.weight(1f),
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis,
                    )
                    Text(
                        text       = stringResource(R.string.home_leaderboard_pts, entry.points),
                        style      = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color      = LocalHomeRedesignPalette.current.onCard,
                    )
                }
                if (index < 2) HorizontalDivider(thickness = 0.5.dp, color = LocalHomeRedesignPalette.current.divider)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Community card + carousel
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun HomeCommunityHighlightCard(
    post: CommunityPost,
    badges: List<UserBadge> = emptyList(),
    onOpenCommunity: () -> Unit,
    onOpenPickup: () -> Unit,
    onOpenMap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier       = modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(GreenHero.CornerCard)),
        shape          = RoundedCornerShape(GreenHero.CornerCard),
        color          = LocalHomeRedesignPalette.current.card,
        tonalElevation = 0.dp,
        border         = BorderStroke(0.5.dp, LocalHomeRedesignPalette.current.chipStroke),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier         = Modifier.size(34.dp).clip(CircleShape).background(LocalHomeRedesignPalette.current.softAccent),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.EnergySavingsLeaf, null, tint = LocalHomeRedesignPalette.current.primary, modifier = Modifier.size(18.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(post.groupName.ifBlank { post.author }, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = LocalHomeRedesignPalette.current.onCard)
                    Text(formatRelativePostTime(post.createdAtMillis), style = MaterialTheme.typography.labelSmall, color = LocalHomeRedesignPalette.current.muted)
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(post.message, style = MaterialTheme.typography.bodyMedium, color = LocalHomeRedesignPalette.current.onCard, lineHeight = 20.sp)
            if (badges.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    badges.filter { it.id != "lake" }.forEach { badge ->
                        AssistChip(
                            onClick = onOpenCommunity,
                            label   = { Text(badge.title, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelSmall) },
                            leadingIcon = { Icon(Icons.Outlined.Stars, null, modifier = Modifier.size(16.dp)) },
                            colors  = AssistChipDefaults.assistChipColors(containerColor = LocalHomeRedesignPalette.current.softAccent, labelColor = LocalHomeRedesignPalette.current.onCard),
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.home_community_likes, post.likes), style = MaterialTheme.typography.labelSmall, color = LocalHomeRedesignPalette.current.muted, modifier = Modifier.clickable { onOpenCommunity() })
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onOpenPickup,
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape   = RoundedCornerShape(GreenHero.CornerButton),
                    border  = BorderStroke(1.5.dp, LocalHomeRedesignPalette.current.primary),
                    colors  = ButtonDefaults.outlinedButtonColors(contentColor = LocalHomeRedesignPalette.current.primary),
                ) {
                    Text(stringResource(R.string.home_request_pickup_capitalized), style = MaterialTheme.typography.labelLarge)
                }
                Button(
                    onClick  = onOpenMap,
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape    = RoundedCornerShape(GreenHero.CornerButton),
                    colors   = ButtonDefaults.buttonColors(containerColor = LocalHomeRedesignPalette.current.primary, contentColor = LocalHomeRedesignPalette.current.onPrimary),
                ) {
                    Text(stringResource(R.string.home_recycle_map), style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
fun HomeCommunityHighlightsCarousel(
    highlights: List<CommunityPost>,
    badges: List<UserBadge>,
    onOpenCommunity: () -> Unit,
    onOpenPickup: () -> Unit,
    onOpenMap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (highlights.isEmpty()) return
    val pagerState = rememberPagerState(pageCount = { highlights.size })

    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(end = 24.dp), pageSpacing = 10.dp) { page ->
            HomeCommunityHighlightCard(
                post            = highlights[page],
                badges          = if (page == 0) badges else emptyList(),
                onOpenCommunity = onOpenCommunity,
                onOpenPickup    = onOpenPickup,
                onOpenMap       = onOpenMap,
                modifier        = Modifier.fillMaxWidth(),
            )
        }
        if (highlights.size > 1) {
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                repeat(highlights.size) { i ->
                    val active = i == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(if (active) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(if (active) LocalHomeRedesignPalette.current.primary else LocalHomeRedesignPalette.current.muted.copy(alpha = 0.35f))
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Category chip
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun HomeCategoryChip(
    title: String,
    icon: ImageVector,
    iconBackground: Color,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick        = onClick,
        modifier       = modifier.height(52.dp),
        shape          = RoundedCornerShape(GreenHero.CornerChip),
        color          = LocalHomeRedesignPalette.current.chipSurface,
        tonalElevation = 0.dp,
        border         = BorderStroke(0.5.dp, LocalHomeRedesignPalette.current.chipStroke),
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier         = Modifier.size(30.dp).clip(CircleShape).background(iconBackground),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(17.dp))
            }
            Text(text = title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = LocalHomeRedesignPalette.current.onCard)
        }
    }
}