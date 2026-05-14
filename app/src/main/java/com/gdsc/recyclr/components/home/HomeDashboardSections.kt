package com.gdsc.recyclr.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.EnergySavingsLeaf
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.gdsc.recyclr.R
import com.gdsc.recyclr.domain.model.engagement.CommunityPost
import com.gdsc.recyclr.domain.model.engagement.EcoStreak
import com.gdsc.recyclr.domain.model.engagement.LeaderboardEntry
import com.gdsc.recyclr.domain.model.engagement.RecWallet
import com.gdsc.recyclr.domain.model.engagement.UserBadge
import com.gdsc.recyclr.domain.model.engagement.WeeklyChallenge
import java.util.concurrent.TimeUnit

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
    val walletGradientStart: Color,
    val walletGradientEnd: Color,
    val pointsStar: Color,
)

private fun ColorScheme.homePaletteIsDark(): Boolean = background.luminance() < 0.5f

private fun buildHomePalette(cs: ColorScheme): HomeRedesignPalette {
    val dark = cs.homePaletteIsDark()
    return HomeRedesignPalette(
        primary = cs.primary,
        onPrimary = cs.onPrimary,
        pageBackground = cs.background,
        card = cs.surface,
        onCard = cs.onSurface,
        muted = cs.onSurfaceVariant,
        greeting = cs.onSurfaceVariant,
        divider = cs.outline.copy(alpha = if (dark) 0.28f else 0.4f),
        highlightRow = cs.primaryContainer.copy(alpha = if (dark) 0.42f else 0.88f),
        softAccent = cs.primaryContainer.copy(alpha = if (dark) 0.5f else 1f),
        streakBanner = if (dark) Color(0xFF2F2818) else Color(0xFFFFF8E1),
        progressTrack = cs.surfaceVariant,
        chipStroke = cs.outline.copy(alpha = if (dark) 0.45f else 0.5f),
        chipSurface = cs.surface,
        walletGradientStart = cs.primaryContainer.copy(alpha = if (dark) 0.55f else 1f),
        walletGradientEnd = cs.surface,
        pointsStar = Color(0xFFFFB300),
    )
}

val LocalHomeRedesignPalette = compositionLocalOf<HomeRedesignPalette> {
    error("Provide LocalHomeRedesignPalette with HomeRedesignThemeProvider")
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

@Composable
fun formatRelativePostTime(createdAtMillis: Long?): String {
    val millis = createdAtMillis ?: return stringResource(R.string.home_time_recent)
    val diff = (System.currentTimeMillis() - millis).coerceAtLeast(0L)
    if (diff < 60_000L) return stringResource(R.string.home_time_just_now)
    val mins = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)
    return when {
        mins < 60 -> stringResource(R.string.home_time_mins_ago, mins.coerceAtLeast(1))
        hours < 24 -> stringResource(R.string.home_time_hours_ago, hours)
        else -> stringResource(R.string.home_time_days_ago, days.coerceAtLeast(1))
    }
}

@Composable
fun HomeGreetingHeader(
    userName: String,
    photoUrl: String?,
    unreadNotificationCount: Int,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onProfileClick, modifier = Modifier.size(48.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(LocalHomeRedesignPalette.current.softAccent),
                contentAlignment = Alignment.Center,
            ) {
                if (!photoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Icon(
                        Icons.Outlined.EnergySavingsLeaf,
                        contentDescription = null,
                        tint = LocalHomeRedesignPalette.current.primary,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }
        Column(modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) {
            Text(
                text = stringResource(R.string.home_welcome_back_comma),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = LocalHomeRedesignPalette.current.greeting,
            )
            Text(
                text = userName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = LocalHomeRedesignPalette.current.onCard,
            )
        }
        IconButton(onClick = onNotificationClick, modifier = Modifier.size(48.dp)) {
            if (unreadNotificationCount > 0) {
                BadgedBox(
                    badge = {
                        Badge(containerColor = MaterialTheme.colorScheme.error) {
                            Text(
                                text = if (unreadNotificationCount > 9) "9+" else unreadNotificationCount.toString(),
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = null,
                        tint = LocalHomeRedesignPalette.current.onCard,
                        modifier = Modifier.size(24.dp),
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = null,
                    tint = LocalHomeRedesignPalette.current.onCard,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}

@Composable
fun HomeStatsRow(
    co2Kg: Float,
    points: Int,
    trees: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        HomeStatCard(
            modifier = Modifier.weight(1f),
            icon = {
                Icon(
                    Icons.Outlined.EnergySavingsLeaf,
                    null,
                    tint = LocalHomeRedesignPalette.current.primary,
                    modifier = Modifier.size(24.dp),
                )
            },
            value = String.format("%.1f kg", co2Kg),
            label = stringResource(R.string.home_stat_co2),
        )
        HomeStatCard(
            modifier = Modifier.weight(1f),
            icon = {
                Icon(
                    Icons.Filled.Star,
                    null,
                    tint = LocalHomeRedesignPalette.current.pointsStar,
                    modifier = Modifier.size(24.dp),
                )
            },
            value = points.toString(),
            label = stringResource(R.string.home_stat_points),
        )
        HomeStatCard(
            modifier = Modifier.weight(1f),
            icon = {
                Icon(
                    Icons.Filled.Park,
                    null,
                    tint = LocalHomeRedesignPalette.current.primary,
                    modifier = Modifier.size(24.dp),
                )
            },
            value = trees.toString(),
            label = stringResource(R.string.home_stat_trees),
        )
    }
}

@Composable
private fun HomeStatCard(
    icon: @Composable () -> Unit,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LocalHomeRedesignPalette.current.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            icon()
            Spacer(Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = LocalHomeRedesignPalette.current.onCard,
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = LocalHomeRedesignPalette.current.muted,
            )
        }
    }
}

@Composable
fun HomeWalletSummaryCard(
    wallet: RecWallet,
    onOpenWallet: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenWallet),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LocalHomeRedesignPalette.current.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            LocalHomeRedesignPalette.current.walletGradientStart,
                            LocalHomeRedesignPalette.current.walletGradientEnd,
                        ),
                    ),
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.home_wallet_rec_title),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalHomeRedesignPalette.current.onCard,
                )
                Text(
                    text = stringResource(R.string.home_wallet_rec_balance, wallet.recBalance),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalHomeRedesignPalette.current.onCard,
                )
                Text(
                    text = stringResource(R.string.home_wallet_co2_credits, wallet.carbonCreditsTonnes),
                    fontSize = 12.sp,
                    color = LocalHomeRedesignPalette.current.muted,
                )
            }
            Text(
                text = stringResource(R.string.home_wallet_view_details_chevron),
                fontSize = 12.sp,
                color = LocalHomeRedesignPalette.current.primary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable(onClick = onOpenWallet),
            )
        }
    }
}

@Composable
fun HomeEcoStreakBanner(
    streak: EcoStreak,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(LocalHomeRedesignPalette.current.streakBanner)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.home_eco_streak_inline, streak.currentDays),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = LocalHomeRedesignPalette.current.onCard,
        )
        Text(
            text = stringResource(R.string.home_streak_multiplier, streak.multiplier),
            fontSize = 12.sp,
            color = LocalHomeRedesignPalette.current.muted,
        )
    }
}

@Composable
fun HomeWeeklyChallengeCard(
    challenge: WeeklyChallenge,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val progress = challenge.currentScans.toFloat() / challenge.targetScans.coerceAtLeast(1)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LocalHomeRedesignPalette.current.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.home_weekly_challenge_title),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalHomeRedesignPalette.current.onCard,
                )
                Text(
                    text = stringResource(R.string.home_challenge_pts, challenge.rewardPoints),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalHomeRedesignPalette.current.primary,
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = challenge.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = LocalHomeRedesignPalette.current.onCard,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = challenge.description,
                fontSize = 12.sp,
                color = LocalHomeRedesignPalette.current.muted,
            )
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = LocalHomeRedesignPalette.current.primary,
                trackColor = LocalHomeRedesignPalette.current.progressTrack,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(
                    R.string.home_weekly_challenge_progress,
                    challenge.currentScans,
                    challenge.targetScans,
                    challenge.endsInDays,
                ),
                fontSize = 12.sp,
                color = LocalHomeRedesignPalette.current.muted,
            )
        }
    }
}

private data class HomeLeaderboardLayoutMetrics(
    val cardPaddingH: Dp,
    val cardPaddingV: Dp,
    val rowVerticalPadding: Dp,
    val headerBottomSpacer: Dp,
    val titleFontSp: TextUnit,
    val rowFontSp: TextUnit,
    val linkFontSp: TextUnit,
)

@Composable
private fun rememberHomeLeaderboardLayoutMetrics(): HomeLeaderboardLayoutMetrics {
    val widthDp = LocalConfiguration.current.screenWidthDp.coerceIn(280, 900)
    return remember(widthDp) {
        val t = ((widthDp - 280f) / 620f).coerceIn(0f, 1f)
        HomeLeaderboardLayoutMetrics(
            cardPaddingH = (12f + 10f * t).dp,
            cardPaddingV = (12f + 6f * t).dp,
            rowVerticalPadding = (8f + 6f * t).dp,
            headerBottomSpacer = (4f + 4f * t).dp,
            titleFontSp = (15f + 3f * t).sp,
            rowFontSp = (13f + 2.5f * t).sp,
            linkFontSp = (11.5f + 1.5f * t).sp,
        )
    }
}

@Composable
fun HomeLeaderboardPreviewCard(
    entries: List<LeaderboardEntry>,
    onOpenLeaderboard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val m = rememberHomeLeaderboardLayoutMetrics()
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LocalHomeRedesignPalette.current.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = m.cardPaddingH, vertical = m.cardPaddingV),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.home_leaderboard),
                    fontSize = m.titleFontSp,
                    fontWeight = FontWeight.Bold,
                    color = LocalHomeRedesignPalette.current.onCard,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                TextButton(onClick = onOpenLeaderboard) {
                    Text(
                        text = stringResource(R.string.home_view_all),
                        fontSize = m.linkFontSp,
                        color = LocalHomeRedesignPalette.current.primary,
                        maxLines = 1,
                    )
                }
            }
            Spacer(Modifier.height(m.headerBottomSpacer))
            val preview = entries.take(3)
            preview.forEachIndexed { index, entry ->
                val isYou = entry.isCurrentUser
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(if (isYou) 8.dp else 0.dp))
                            .background(if (isYou) LocalHomeRedesignPalette.current.highlightRow else Color.Transparent)
                            .padding(vertical = m.rowVerticalPadding),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(
                                R.string.home_leaderboard_row,
                                entry.rank,
                                if (isYou) stringResource(R.string.home_leaderboard_you) else entry.name,
                            ),
                            fontSize = m.rowFontSp,
                            color = LocalHomeRedesignPalette.current.onCard,
                            fontWeight = if (isYou) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = stringResource(R.string.home_leaderboard_pts, entry.points),
                            fontSize = m.rowFontSp,
                            fontWeight = FontWeight.Medium,
                            color = LocalHomeRedesignPalette.current.onCard,
                            maxLines = 1,
                        )
                    }
                    if (index < preview.lastIndex) {
                        HorizontalDivider(thickness = 0.5.dp, color = LocalHomeRedesignPalette.current.divider)
                    }
                }
            }
        }
    }
}

@Composable
fun HomeCommunityHighlightCard(
    post: CommunityPost,
    badges: List<UserBadge> = emptyList(),
    onOpenCommunity: () -> Unit,
    onOpenPickup: () -> Unit,
    onOpenMap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LocalHomeRedesignPalette.current.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(LocalHomeRedesignPalette.current.softAccent),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Outlined.EnergySavingsLeaf,
                        contentDescription = null,
                        tint = LocalHomeRedesignPalette.current.primary,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.groupName.ifBlank { post.author },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = LocalHomeRedesignPalette.current.onCard,
                    )
                    Text(
                        text = formatRelativePostTime(post.createdAtMillis),
                        fontSize = 12.sp,
                        color = LocalHomeRedesignPalette.current.muted,
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = post.message,
                fontSize = 14.sp,
                color = LocalHomeRedesignPalette.current.onCard,
            )
            if (badges.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    badges.forEach { badge ->
                        AssistChip(
                            onClick = onOpenCommunity,
                            label = {
                                Text(
                                    badge.title,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Stars,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = LocalHomeRedesignPalette.current.softAccent,
                                labelColor = LocalHomeRedesignPalette.current.onCard,
                            ),
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.home_community_likes, post.likes),
                fontSize = 12.sp,
                color = LocalHomeRedesignPalette.current.muted,
                modifier = Modifier.clickable { onOpenCommunity() },
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = onOpenPickup,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, LocalHomeRedesignPalette.current.primary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LocalHomeRedesignPalette.current.primary),
                ) {
                    Text(
                        text = stringResource(R.string.home_request_pickup_capitalized),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Button(
                    onClick = onOpenMap,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LocalHomeRedesignPalette.current.primary,
                        contentColor = LocalHomeRedesignPalette.current.onPrimary,
                    ),
                ) {
                    Text(
                        text = stringResource(R.string.home_recycle_map),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
fun HomeCategoryChip(
    title: String,
    icon: ImageVector,
    iconBackground: Color,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(LocalHomeRedesignPalette.current.chipSurface)
            .border(1.dp, LocalHomeRedesignPalette.current.chipStroke, RoundedCornerShape(32.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(iconBackground),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.width(8.dp))
        Text(text = title, fontSize = 14.sp, color = LocalHomeRedesignPalette.current.onCard)
    }
}
