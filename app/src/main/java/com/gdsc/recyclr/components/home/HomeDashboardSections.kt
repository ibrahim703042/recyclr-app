package com.gdsc.recyclr.components.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gdsc.recyclr.R
import com.gdsc.recyclr.domain.model.engagement.CommunityPost
import com.gdsc.recyclr.domain.model.engagement.EcoStreak
import com.gdsc.recyclr.domain.model.engagement.LeaderboardEntry
import com.gdsc.recyclr.domain.model.engagement.RecWallet
import com.gdsc.recyclr.domain.model.engagement.WeeklyChallenge
import com.gdsc.recyclr.ui.theme.LeafGreen
import com.gdsc.recyclr.ui.theme.RecyclrThemeColors
import com.gdsc.recyclr.ui.theme.recyclrMutedText

@Composable
fun HomeStreakAndWalletRow(
    streak: EcoStreak,
    wallet: RecWallet,
    onOpenWallet: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(18.dp),
            backgroundColor = RecyclrThemeColors.pointsCard,
            elevation = 0.dp,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = stringResource(R.string.home_streak_title), fontWeight = FontWeight.Bold)
                Text(text = stringResource(R.string.home_streak_days, streak.currentDays), fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text(text = stringResource(R.string.home_streak_multiplier, streak.multiplier), fontSize = 12.sp, color = recyclrMutedText())
            }
        }
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onOpenWallet),
            shape = RoundedCornerShape(18.dp),
            backgroundColor = MaterialTheme.colors.surface,
            elevation = 0.dp,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = stringResource(R.string.home_rec_wallet), fontWeight = FontWeight.Bold)
                Text(text = stringResource(R.string.home_rec_balance, wallet.recBalance), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = LeafGreen)
                Text(text = stringResource(R.string.home_carbon_credits, wallet.carbonCreditsTonnes), fontSize = 12.sp, color = recyclrMutedText())
            }
        }
    }
}

@Composable
fun HomeChallengeBanner(
    challenge: WeeklyChallenge,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val progress = challenge.currentScans.toFloat() / challenge.targetScans.coerceAtLeast(1)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(text = stringResource(R.string.home_active_challenge), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = challenge.title, fontWeight = FontWeight.SemiBold)
            Text(text = challenge.description, fontSize = 13.sp, color = recyclrMutedText())
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = progress.coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = LeafGreen,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(
                    R.string.home_challenge_progress,
                    challenge.currentScans,
                    challenge.targetScans,
                    challenge.endsInDays,
                ),
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
fun HomeLeaderboardPreview(
    entries: List<LeaderboardEntry>,
    onOpenLeaderboard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenLeaderboard),
        shape = RoundedCornerShape(20.dp),
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(text = stringResource(R.string.home_leaderboard), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(10.dp))
            entries.forEach { entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "#${entry.rank} ${entry.name}",
                        fontWeight = if (entry.isCurrentUser) FontWeight.Bold else FontWeight.Normal,
                    )
                    Text(text = "${entry.points}")
                }
            }
        }
    }
}

@Composable
fun HomeCommunityPreview(
    post: CommunityPost,
    onOpenCommunity: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenCommunity),
        shape = RoundedCornerShape(20.dp),
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(text = stringResource(R.string.home_community), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = post.groupName, fontSize = 12.sp, color = LeafGreen)
            Text(text = post.author, fontWeight = FontWeight.SemiBold)
            Text(text = post.message, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = stringResource(R.string.home_post_likes, post.likes), fontSize = 12.sp, color = recyclrMutedText())
        }
    }
}

@Composable
fun HomeQuickActionsRow(
    onOpenPickup: () -> Unit,
    onOpenMap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        QuickActionChip(
            label = stringResource(R.string.home_request_pickup),
            modifier = Modifier.weight(1f),
            onClick = onOpenPickup,
        )
        QuickActionChip(
            label = stringResource(R.string.home_open_map),
            modifier = Modifier.weight(1f),
            onClick = onOpenMap,
        )
    }
}

@Composable
private fun QuickActionChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = RecyclrThemeColors.scanCard,
        elevation = 0.dp,
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 16.dp),
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
        )
    }
}
