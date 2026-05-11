package com.gdsc.recyclr.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.design.RecyclrCategoryRow
import com.gdsc.recyclr.components.home.HomeChallengeBanner
import com.gdsc.recyclr.components.home.HomeCommunityPreview
import com.gdsc.recyclr.components.home.HomeLeaderboardPreview
import com.gdsc.recyclr.components.home.HomeQuickActionsRow
import com.gdsc.recyclr.components.home.HomeStreakAndWalletRow
import com.gdsc.recyclr.components.preferences.ThemeToggleIconButton
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.UserImpact
import com.gdsc.recyclr.domain.model.engagement.HomeDashboard
import com.gdsc.recyclr.ui.theme.LeafGreen
import com.gdsc.recyclr.ui.theme.RecyclrThemeColors
import com.gdsc.recyclr.ui.theme.recyclrMutedText
import com.gdsc.recyclr.ui.theme.recyclrScreenBackground

@Composable
private fun homeCategories(): List<HomeCategory> = listOf(
    HomeCategory("plastic", stringResource(R.string.category_plastic), stringResource(R.string.category_plastic_subtitle), RecyclrThemeColors.categoryPlastic, Icons.Default.LocalDrink),
    HomeCategory("paper", stringResource(R.string.category_paper), stringResource(R.string.category_paper_subtitle), RecyclrThemeColors.categoryPaper, Icons.Default.Newspaper),
    HomeCategory("glass", stringResource(R.string.category_glass), stringResource(R.string.category_glass_subtitle), RecyclrThemeColors.categoryGlass, Icons.Default.WineBar),
    HomeCategory("metal", stringResource(R.string.category_metal), stringResource(R.string.category_metal_subtitle), RecyclrThemeColors.categoryMetal, Icons.Default.LocalDrink),
    HomeCategory("textile", stringResource(R.string.category_textile), stringResource(R.string.category_textile_subtitle), RecyclrThemeColors.categoryTextile, Icons.Default.Checkroom),
)

private data class HomeCategory(
    val routeKey: String,
    val title: String,
    val subtitle: String,
    val iconBackground: Color,
    val icon: ImageVector,
)

@Composable
fun HomeContent(
    padding: PaddingValues,
    impactResponse: Response<UserImpact>,
    dashboardResponse: Response<HomeDashboard>,
    onOpenCategory: (String) -> Unit,
    onOpenScan: () -> Unit,
    onOpenChallenge: () -> Unit,
    onOpenLeaderboard: () -> Unit,
    onOpenCommunity: () -> Unit,
    onOpenWallet: () -> Unit,
    onOpenPickup: () -> Unit,
    onOpenMap: () -> Unit,
) {
    val categories = homeCategories()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(recyclrScreenBackground())
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.app_name),
                color = LeafGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
            )
            ThemeToggleIconButton()
        }

        Spacer(modifier = Modifier.height(18.dp))
        Text(text = stringResource(R.string.home_welcome_back), fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PointsCard(impactResponse = impactResponse, modifier = Modifier.weight(1f))
            ScanShortcutCard(
                onClick = onOpenScan,
                modifier = Modifier
                    .width(110.dp)
                    .height(150.dp),
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        when (val dashboard = dashboardResponse) {
            is Response.Success -> {
                val data = dashboard.data
                if (data != null) {
                HomeStreakAndWalletRow(
                    streak = data.streak,
                    wallet = data.wallet,
                    onOpenWallet = onOpenWallet,
                )
                Spacer(modifier = Modifier.height(16.dp))
                HomeChallengeBanner(challenge = data.challenge, onClick = onOpenChallenge)
                Spacer(modifier = Modifier.height(16.dp))
                HomeLeaderboardPreview(entries = data.leaderboard, onOpenLeaderboard = onOpenLeaderboard)
                Spacer(modifier = Modifier.height(16.dp))
                data.communityPreview?.let { post ->
                    HomeCommunityPreview(post = post, onOpenCommunity = onOpenCommunity)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                HomeQuickActionsRow(onOpenPickup = onOpenPickup, onOpenMap = onOpenMap)
                Spacer(modifier = Modifier.height(20.dp))
                }
            }
            is Response.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(vertical = 8.dp))
                Spacer(modifier = Modifier.height(20.dp))
            }
            is Response.Failure -> Unit
        }

        Text(text = stringResource(R.string.home_categories), fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            categories.forEach { category ->
                RecyclrCategoryRow(
                    title = category.title,
                    subtitle = category.subtitle,
                    iconBackground = category.iconBackground,
                    icon = category.icon,
                    onClick = { onOpenCategory(category.routeKey) },
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun PointsCard(
    impactResponse: Response<UserImpact>,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.height(150.dp),
        shape = RoundedCornerShape(18.dp),
        backgroundColor = RecyclrThemeColors.pointsCard,
        elevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.reward),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(52.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = stringResource(R.string.home_points_earned), fontSize = 13.sp)
                when (impactResponse) {
                    is Response.Loading -> CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    is Response.Failure -> Text(text = "106", fontWeight = FontWeight.Bold, fontSize = 28.sp)
                    is Response.Success -> Text(
                        text = "${impactResponse.data?.pointsBalance ?: 0}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                    )
                }
                Text(text = stringResource(R.string.home_points_label), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = stringResource(R.string.home_points_last_week), fontSize = 11.sp, color = recyclrMutedText())
            }
        }
    }
}

@Composable
private fun ScanShortcutCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        backgroundColor = RecyclrThemeColors.scanCard,
        elevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.scan),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(42.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = stringResource(R.string.home_scan), fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }
    }
}
