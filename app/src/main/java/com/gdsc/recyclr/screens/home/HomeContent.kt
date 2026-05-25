@file:Suppress("DEPRECATION")
@file:OptIn(androidx.compose.material.ExperimentalMaterialApi::class)

package com.gdsc.recyclr.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.home.*
import com.gdsc.recyclr.data.service.LiveActivity
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.UserImpact
import com.gdsc.recyclr.domain.model.UserRole
import com.gdsc.recyclr.domain.model.engagement.HomeDashboard
import com.gdsc.recyclr.ui.theme.RecyclrThemeColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ── Category model ─────────────────────────────────────────────────────────────
private data class HomeCategory(
    val routeKey: String,
    val title: String,
    val iconBackground: Color,
    val iconColor: Color,
    val icon: ImageVector,
)

@Composable
private fun homeCategories(): List<HomeCategory> = listOf(
    HomeCategory("plastic",  stringResource(R.string.category_plastic),  RecyclrThemeColors.categoryPlastic,  RecyclrThemeColors.categoryPlasticIcon,  Icons.Default.LocalDrink),
    HomeCategory("paper",    stringResource(R.string.category_paper),    RecyclrThemeColors.categoryPaper,    RecyclrThemeColors.categoryPaperIcon,    Icons.Default.Newspaper),
    HomeCategory("glass",    stringResource(R.string.category_glass),    RecyclrThemeColors.categoryGlass,    RecyclrThemeColors.categoryGlassIcon,    Icons.Default.WineBar),
    HomeCategory("metal",    stringResource(R.string.category_metal),    RecyclrThemeColors.categoryMetal,    RecyclrThemeColors.categoryMetalIcon,    Icons.Default.LocalDrink),
    HomeCategory("textile",  stringResource(R.string.category_textile),  RecyclrThemeColors.categoryTextile,  RecyclrThemeColors.categoryTextileIcon,  Icons.Default.Checkroom),
)

// ── HomeContent ────────────────────────────────────────────────────────────────
@Composable
fun HomeContent(
    padding: PaddingValues,
    impactResponse: Response<UserImpact>,
    dashboardResponse: Response<HomeDashboard>,
    userName: String,
    userRole: UserRole,
    latestLiveActivity: LiveActivity?,
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
    onOpenCollectorDashboard: () -> Unit = {},
) {
    val categories = homeCategories()
    val scope = rememberCoroutineScope()
    var pullRefreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = pullRefreshing,
        onRefresh  = {
            pullRefreshing = true
            onRefresh()
            scope.launch { delay(450); pullRefreshing = false }
        },
    )

    val impact  = (impactResponse as? Response.Success)?.data
    val co2Kg   = impact?.co2SavedKg   ?: 0f
    val points  = impact?.pointsBalance ?: 0
    val trees   = impact?.treesEquivalent ?: 0

    var visibleLiveActivity by remember { mutableStateOf<LiveActivity?>(null) }
    LaunchedEffect(latestLiveActivity?.userName, latestLiveActivity?.itemType, latestLiveActivity?.timestampMillis) {
        val activity = latestLiveActivity ?: run {
            visibleLiveActivity = null
            return@LaunchedEffect
        }
        visibleLiveActivity = activity
        delay(10_000)
        visibleLiveActivity = null
    }

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
                contentPadding = PaddingValues(bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {

                // ── Green header banner ──────────────────────────────────────
                item(key = "header") {
                    HomeGreetingHeader(
                        userName                = userName,
                        photoUrl                = photoUrl,
                        unreadNotificationCount = unreadNotificationCount,
                        liveActivity            = visibleLiveActivity,
                        onNotificationClick     = onOpenNotifications,
                        onProfileClick          = onOpenProfile,
                    )
                }

                // Collector shortcut
                if (userRole == UserRole.COLLECTOR || userRole == UserRole.ADMIN) {
                    item(key = "collector_shortcut") {
                        HomeCollectorShortcut(
                            onClick  = onOpenCollectorDashboard,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        )
                    }
                }

                // Stats row — léger chevauchement sur l’en-tête vert
                item(key = "stats") {
                    HomeStatsRow(
                        co2Kg    = co2Kg,
                        points   = points,
                        trees    = trees,
                        modifier = Modifier
                            .zIndex(1f)
                            .offset(y = (-10).dp)
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 12.dp),
                    )
                }

                // Quick Action Buttons
                item(key = "quick_actions") {
                    HomeQuickActionsRow(
                        onScan = onOpenScan,
                        onMap = onOpenMap,
                        onPickup = onOpenPickup,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp)
                    )
                }

                // Dashboard-dependent cards
                when (val dashboard = dashboardResponse) {
                    is Response.Success -> {
                        val data = dashboard.data
                        if (data != null) {
                            item(key = "wallet") {
                                HomeWalletSummaryCard(
                                    wallet       = data.wallet,
                                    onOpenWallet = onOpenWallet,
                                    modifier     = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp),
                                )
                            }
                            item(key = "streak") {
                                HomeEcoStreakBanner(
                                    streak   = data.streak,
                                    modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp),
                                )
                            }
                            item(key = "challenge_leaderboard") {
                                HomeSectionTitle(
                                    text     = stringResource(R.string.home_weekly_challenge_title),
                                    modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp),
                                )
                                HomeChallengeLeaderboardCarousel(
                                    challenge        = data.challenge,
                                    entries          = data.leaderboard,
                                    onOpenChallenge  = onOpenChallenge,
                                    onOpenLeaderboard = onOpenLeaderboard,
                                    modifier         = Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp),
                                )
                            }
                            if (data.communityHighlights.isNotEmpty()) {
                                item(key = "community") {
                                    HomeSectionTitle(
                                        text     = stringResource(R.string.home_community_this_week),
                                        modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp),
                                    )
                                    HomeCommunityHighlightsCarousel(
                                        highlights      = data.communityHighlights,
                                        badges          = data.badges.filter { it.earned }.take(8),
                                        onOpenCommunity = onOpenCommunity,
                                        onOpenPickup    = onOpenPickup,
                                        onOpenMap       = onOpenMap,
                                        modifier        = Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp),
                                    )
                                }
                            }
                        }
                    }
                    is Response.Loading -> {
                        item(key = "loading") {
                            Box(
                                modifier         = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(color = LocalHomeRedesignPalette.current.primary)
                            }
                        }
                    }
                    is Response.Failure -> Unit
                }

                // Categories
                item(key = "cat_title") {
                    HomeSectionTitle(
                        text     = stringResource(R.string.home_recycling_categories),
                        modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp),
                    )
                }
                item(key = "categories_row") {
                    LazyRow(
                        contentPadding        = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier              = Modifier.padding(bottom = 8.dp),
                    ) {
                        items(categories) { cat ->
                            HomeCategoryChip(
                                title          = cat.title,
                                icon           = cat.icon,
                                iconBackground = cat.iconBackground,
                                iconTint       = cat.iconColor,
                                onClick        = { onOpenCategory(cat.routeKey) },
                            )
                        }
                    }
                }

                item(key = "bottom_spacer") { Spacer(Modifier.height(16.dp)) }
            }

            PullRefreshIndicator(
                refreshing   = pullRefreshing,
                state        = pullRefreshState,
                modifier     = Modifier.align(Alignment.TopCenter),
                contentColor = LocalHomeRedesignPalette.current.primary,
            )
        }
    }
}