@file:Suppress("DEPRECATION")
@file:OptIn(
    androidx.compose.material.ExperimentalMaterialApi::class,
    androidx.compose.material3.ExperimentalMaterial3Api::class,
)

package com.gdsc.recyclr.screens.shop

import android.content.Intent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gdsc.recyclr.R
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.ShopItem
import com.gdsc.recyclr.screens.engagement.DonationHubSection
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ShopScreen(
    onOpenSupportChat: () -> Unit = {},
    viewModel: ShopViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val supportUnread by viewModel.supportUnreadCount.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val shopItemsResponse = viewModel.shopItemsResponse
    val redeemResponse = viewModel.redeemResponse
    val scope = rememberCoroutineScope()

    var selectedItem by remember { mutableStateOf<ShopItem?>(null) }
    var showConfirmation by remember { mutableStateOf(false) }
    var showDonations by remember { mutableStateOf(false) }
    var selectedCategoryKey by remember { mutableStateOf<String?>(null) }
    var refreshing by remember { mutableStateOf(false) }
    val pullState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = {
            refreshing = true
            viewModel.refreshShop()
            scope.launch {
                delay(500)
                refreshing = false
            }
        },
    )

    LaunchedEffect(redeemResponse) {
        when (redeemResponse) {
            is Response.Failure -> {
                snackbarHostState.showSnackbar(
                    redeemResponse.e.message ?: context.getString(R.string.shop_redemption_failed),
                )
                viewModel.acknowledgeRedeemFeedback()
            }
            is Response.Success -> {
                if (redeemResponse.data != null) {
                    showConfirmation = true
                    viewModel.acknowledgeRedeemFeedback()
                }
            }
            else -> Unit
        }
    }

    val allItems = (shopItemsResponse as? Response.Success)?.data.orEmpty()
    val filteredItems = remember(allItems, selectedCategoryKey) {
        val key = selectedCategoryKey
        if (key.isNullOrBlank()) allItems else allItems.filter { it.category.equals(key, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (selectedItem == null) {
                        Column {
                            Text(
                                stringResource(R.string.shop_marketplace_title),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                stringResource(R.string.shop_points_available, viewModel.pointsBalance),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    } else {
                        Text(
                            stringResource(R.string.shop_product_details_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
                navigationIcon = {
                    if (selectedItem != null) {
                        IconButton(onClick = { selectedItem = null }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    }
                },
                actions = {
                    if (selectedItem == null) {
                        IconButton(onClick = onOpenSupportChat) {
                            if (supportUnread > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge {
                                            Text(
                                                text = if (supportUnread > 9) "9+" else supportUnread.toString(),
                                                style = MaterialTheme.typography.labelSmall,
                                            )
                                        }
                                    },
                                ) {
                                    Icon(
                                        Icons.Outlined.Chat,
                                        contentDescription = stringResource(R.string.shop_support_chat),
                                    )
                                }
                            } else {
                                Icon(
                                    Icons.Outlined.Chat,
                                    contentDescription = stringResource(R.string.shop_support_chat),
                                )
                            }
                        }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pullRefresh(pullState),
        ) {
            if (selectedItem == null) {
                Column(Modifier.fillMaxSize()) {
                    TabRow(
                        selectedTabIndex = if (showDonations) 1 else 0,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                        divider = {},
                    ) {
                        Tab(
                            selected = !showDonations,
                            onClick = { showDonations = false },
                            text = { Text(stringResource(R.string.shop_tab_items)) },
                        )
                        Tab(
                            selected = showDonations,
                            onClick = { showDonations = true },
                            text = { Text(stringResource(R.string.shop_tab_donations)) },
                        )
                    }

                    if (showDonations) {
                        Box(Modifier.padding(16.dp).fillMaxSize()) {
                            DonationHubSection(
                                onSupportCause = { cause ->
                                    viewModel.redeem(
                                        ShopItem(
                                            id = cause.id,
                                            title = cause.title,
                                            price = cause.pointsCost,
                                            description = cause.description,
                                            category = cause.category,
                                            imageUrl = "",
                                            isDonation = true
                                        )
                                    )
                                }
                            )
                        }
                    } else {
                        CategoryChipRow(
                            selectedKey = selectedCategoryKey,
                            onSelect = { selectedCategoryKey = it },
                        )
                        when (shopItemsResponse) {
                            is Response.Loading -> {
                                ShopGridShimmer()
                            }
                            is Response.Failure -> {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(stringResource(R.string.shop_load_failed))
                                }
                            }
                            is Response.Success -> {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(3),
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    items(filteredItems, key = { it.id }) { item ->
                                        ShopItemCard(
                                            shopItem = item,
                                            onItemClick = { selectedItem = it },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                val item = selectedItem!!
                val related = allItems.filter { it.id != item.id }.take(3)
                ProductDetailsContent(
                    item = item,
                    userPoints = viewModel.pointsBalance,
                    wishlisted = viewModel.isWishlisted(item.id),
                    relatedItems = related,
                    onShare = {
                        val send = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                context.getString(R.string.product_share_text, item.title, item.price),
                            )
                        }
                        context.startActivity(Intent.createChooser(send, context.getString(R.string.product_share_title)))
                    },
                    onToggleWishlist = { viewModel.toggleWishlist(item.id) },
                    onPickRelated = { rel -> selectedItem = rel },
                    onConfirmRedeem = { it, _ ->
                        viewModel.redeem(it)
                    },
                )
            }
            PullRefreshIndicator(
                refreshing = refreshing,
                state = pullState,
                modifier = Modifier.align(Alignment.TopCenter),
                contentColor = MaterialTheme.colorScheme.primary,
            )
        }
    }

    if (showConfirmation) {
        AlertDialog(
            onDismissRequest = { showConfirmation = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmation = false
                        selectedItem = null
                    },
                ) { Text(stringResource(R.string.shop_ok)) }
            },
            title = { Text(stringResource(R.string.shop_confirmation_title)) },
            text = { Text(stringResource(R.string.shop_confirmation_message)) },
            icon = {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            },
        )
    }
}

@Composable
private fun CategoryChipRow(
    selectedKey: String?,
    onSelect: (String?) -> Unit,
) {
    val scroll = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scroll)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val chips = listOf(
            null to stringResource(R.string.shop_category_all),
            "nature" to stringResource(R.string.shop_category_nature),
            "bags" to stringResource(R.string.shop_category_bags),
            "education" to stringResource(R.string.shop_category_education),
            "home" to stringResource(R.string.shop_category_home),
        )
        chips.forEach { (key, label) ->
            val selected = key == selectedKey || (key == null && selectedKey == null)
            AssistChip(
                onClick = { onSelect(key) },
                label = { Text(label) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant,
                ),
                shape = RoundedCornerShape(50),
            )
        }
    }
}

@Composable
private fun ShopGridShimmer() {
    val t = rememberInfiniteTransition(label = "shimmer")
    val a by t.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(tween(700, easing = LinearEasing), RepeatMode.Reverse),
        label = "a",
    )
    val base = Color.Gray.copy(alpha = a)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
    ) {
        repeat(4) { row ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(base),
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}
