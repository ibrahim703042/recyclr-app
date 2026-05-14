@file:Suppress("DEPRECATION")
@file:OptIn(androidx.compose.material.ExperimentalMaterialApi::class)

package com.gdsc.recyclr.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gdsc.recyclr.R
import com.gdsc.recyclr.ui.theme.RecyclrThemeColors
import com.gdsc.recyclr.components.home.HomeCategoryChip
import com.gdsc.recyclr.components.home.HomeCommunityHighlightsCarousel
import com.gdsc.recyclr.components.home.HomeEcoStreakBanner
import com.gdsc.recyclr.components.home.HomeGreetingHeader
import com.gdsc.recyclr.components.home.HomeLeaderboardPreviewCard
import com.gdsc.recyclr.components.home.HomeRedesignThemeProvider
import com.gdsc.recyclr.components.home.LocalHomeRedesignPalette
import com.gdsc.recyclr.components.home.HomeStatsRow
import com.gdsc.recyclr.components.home.HomeWalletSummaryCard
import com.gdsc.recyclr.components.home.HomeWeeklyChallengeCard
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.UserImpact
import com.gdsc.recyclr.domain.model.engagement.HomeDashboard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class HomeCategory(
    val routeKey: String,
    val title: String,
    val iconBackground: Color,
    val iconColor: Color,
    val icon: ImageVector,
)

@Composable
private fun homeCategories(): List<HomeCategory> = listOf(
    HomeCategory("plastic", stringResource(R.string.category_plastic), RecyclrThemeColors.categoryPlastic, RecyclrThemeColors.categoryPlasticIcon, Icons.Default.LocalDrink),
    HomeCategory("paper", stringResource(R.string.category_paper), RecyclrThemeColors.categoryPaper, RecyclrThemeColors.categoryPaperIcon, Icons.Default.Newspaper),
    HomeCategory("glass", stringResource(R.string.category_glass), RecyclrThemeColors.categoryGlass, RecyclrThemeColors.categoryGlassIcon, Icons.Default.WineBar),
    HomeCategory("metal", stringResource(R.string.category_metal), RecyclrThemeColors.categoryMetal, RecyclrThemeColors.categoryMetalIcon, Icons.Default.LocalDrink),
    HomeCategory("textile", stringResource(R.string.category_textile), RecyclrThemeColors.categoryTextile, RecyclrThemeColors.categoryTextileIcon, Icons.Default.Checkroom),
)

@Composable
fun HomeContent(
    padding: PaddingValues,
    impactResponse: Response<UserImpact>,
    dashboardResponse: Response<HomeDashboard>,
    userName: String,
    photoUrl: String?,
    unreadNotificationCount: Int,
    onRefresh: () -> Unit,
    onOpenCategory: (String) -> Unit,
    onOpenScan: () -> Unit,
    onOpenChallenge: () -> Unit,
    onOpenLeaderboard: () -> Unit,
    onOpenCommunity: () -> Unit,
    onOpenWallet: () -> Unit,
    onOpenPickup: () -> Unit,
    onOpenMap: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenNotifications: () -> Unit,
) {
    val categories = homeCategories()
    val scope = rememberCoroutineScope()
    var pullRefreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = pullRefreshing,
        onRefresh = {
            pullRefreshing = true
            onRefresh()
            scope.launch {
                delay(450)
                pullRefreshing = false
            }
        },
    )

    val impact = (impactResponse as? Response.Success)?.data
    val co2Kg = impact?.co2SavedKg ?: 0f
    val points = impact?.pointsBalance ?: 0
    val trees = impact?.treesEquivalent ?: 0

    HomeRedesignThemeProvider {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalHomeRedesignPalette.current.pageBackground)
            .padding(padding)
            .pullRefresh(pullRefreshState),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "header") {
                HomeGreetingHeader(
                    userName = userName,
                    photoUrl = photoUrl,
                    unreadNotificationCount = unreadNotificationCount,
                    onNotificationClick = onOpenNotifications,
                    onProfileClick = onOpenProfile,
                )
            }

            item(key = "stats") {
                HomeStatsRow(
                    co2Kg = co2Kg,
                    points = points,
                    trees = trees,
                )
            }

            when (val dashboard = dashboardResponse) {
                is Response.Success -> {
                    val data = dashboard.data
                    if (data != null) {
                        item(key = "wallet") {
                            HomeWalletSummaryCard(
                                wallet = data.wallet,
                                onOpenWallet = onOpenWallet,
                            )
                        }
                        item(key = "streak") {
                            HomeEcoStreakBanner(streak = data.streak)
                        }
                        item(key = "challenge") {
                            HomeWeeklyChallengeCard(
                                challenge = data.challenge,
                                onClick = onOpenChallenge,
                            )
                        }
                        item(key = "leaderboard") {
                            HomeLeaderboardPreviewCard(
                                entries = data.leaderboard,
                                onOpenLeaderboard = onOpenLeaderboard,
                            )
                        }
                        if (data.communityHighlights.isNotEmpty()) {
                            item(key = "community") {
                                HomeCommunityHighlightsCarousel(
                                    highlights = data.communityHighlights,
                                    badges = data.badges.filter { it.earned }.take(8),
                                    onOpenCommunity = onOpenCommunity,
                                    onOpenPickup = onOpenPickup,
                                    onOpenMap = onOpenMap,
                                )
                            }
                        }
                    }
                }
                is Response.Loading -> {
                    item(key = "loading") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(color = LocalHomeRedesignPalette.current.primary)
                        }
                    }
                }
                is Response.Failure -> Unit
            }

            item(key = "cat_title") {
                Text(
                    text = stringResource(R.string.home_recycling_categories),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LocalHomeRedesignPalette.current.onCard,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            item(key = "categories_row") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    categories.forEach { cat ->
                        HomeCategoryChip(
                            title = cat.title,
                            icon = cat.icon,
                            iconBackground = cat.iconBackground,
                            iconTint = cat.iconColor,
                            onClick = { onOpenCategory(cat.routeKey) },
                        )
                    }
                }
            }

            item(key = "bottom_spacer") {
                Spacer(Modifier.height(24.dp))
            }
        }

        PullRefreshIndicator(
            refreshing = pullRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            contentColor = LocalHomeRedesignPalette.current.primary,
        )
    }
    }
}
