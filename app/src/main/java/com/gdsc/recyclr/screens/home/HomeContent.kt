package com.gdsc.recyclr.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.composable.RecyclrTopBar
import com.gdsc.recyclr.components.composable.SectionHeader
import com.gdsc.recyclr.components.design.RecyclrCategoryRow
import com.gdsc.recyclr.components.home.*
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.UserImpact
import com.gdsc.recyclr.domain.model.engagement.HomeDashboard
import com.gdsc.recyclr.ui.theme.RecyclrThemeColors

@Composable
private fun homeCategories(): List<HomeCategory> = listOf(
    HomeCategory("plastic", stringResource(R.string.category_plastic), stringResource(R.string.category_plastic_subtitle), RecyclrThemeColors.categoryPlastic, RecyclrThemeColors.categoryPlasticIcon, Icons.Default.LocalDrink),
    HomeCategory("paper", stringResource(R.string.category_paper), stringResource(R.string.category_paper_subtitle), RecyclrThemeColors.categoryPaper, RecyclrThemeColors.categoryPaperIcon, Icons.Default.Newspaper),
    HomeCategory("glass", stringResource(R.string.category_glass), stringResource(R.string.category_glass_subtitle), RecyclrThemeColors.categoryGlass, RecyclrThemeColors.categoryGlassIcon, Icons.Default.WineBar),
    HomeCategory("metal", stringResource(R.string.category_metal), stringResource(R.string.category_metal_subtitle), RecyclrThemeColors.categoryMetal, RecyclrThemeColors.categoryMetalIcon, Icons.Default.LocalDrink),
    HomeCategory("textile", stringResource(R.string.category_textile), stringResource(R.string.category_textile_subtitle), RecyclrThemeColors.categoryTextile, RecyclrThemeColors.categoryTextileIcon, Icons.Default.Checkroom),
)

private data class HomeCategory(
    val routeKey: String,
    val title: String,
    val subtitle: String,
    val iconBackground: Color,
    val iconColor: Color,
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
            .background(MaterialTheme.colorScheme.background)
            .padding(padding)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RecyclrTopBar(
            title = stringResource(R.string.app_name)
        )

        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            SectionHeader(
                title = stringResource(R.string.home_welcome_back),
                subtitle = "Let's make the world cleaner today."
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                PointsCard(impactResponse = impactResponse, modifier = Modifier.weight(1f))
                ScanShortcutCard(
                    onClick = onOpenScan,
                    modifier = Modifier.weight(0.6f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            when (val dashboard = dashboardResponse) {
                is Response.Success -> {
                    val data = dashboard.data
                    if (data != null) {
                        HomeStreakAndWalletRow(
                            streak = data.streak,
                            wallet = data.wallet,
                            onOpenWallet = onOpenWallet,
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        HomeChallengeBanner(challenge = data.challenge, onClick = onOpenChallenge)
                        Spacer(modifier = Modifier.height(20.dp))
                        HomeLeaderboardPreview(entries = data.leaderboard, onOpenLeaderboard = onOpenLeaderboard)
                        Spacer(modifier = Modifier.height(20.dp))
                        data.communityPreview?.let { post ->
                            HomeCommunityPreview(post = post, onOpenCommunity = onOpenCommunity)
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                        HomeQuickActionsRow(onOpenPickup = onOpenPickup, onOpenMap = onOpenMap)
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
                is Response.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is Response.Failure -> Unit
            }

            SectionHeader(title = stringResource(R.string.home_categories))
            Spacer(modifier = Modifier.height(4.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                categories.forEach { category ->
                    RecyclrCategoryRow(
                        title = category.title,
                        subtitle = category.subtitle,
                        iconBackground = category.iconBackground,
                        iconColor = category.iconColor,
                        icon = category.icon,
                        onClick = { onOpenCategory(category.routeKey) },
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PointsCard(
    impactResponse: Response<UserImpact>,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier.height(160.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.reward),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(48.dp),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Column {
                Text(
                    text = stringResource(R.string.home_points_earned),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                when (impactResponse) {
                    is Response.Loading -> CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    is Response.Failure -> Text(
                        text = "106",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black
                    )
                    is Response.Success -> Text(
                        text = "${impactResponse.data?.pointsBalance ?: 0}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun ScanShortcutCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier.height(160.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.scan),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(56.dp),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.home_scan),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
