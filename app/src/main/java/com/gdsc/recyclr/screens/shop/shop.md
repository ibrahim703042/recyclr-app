package com.gdsc.recyclr.screens.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gdsc.recyclr.R
import com.gdsc.recyclr.domain.model.ShopItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopItemCard(
shopItem: ShopItem,
onItemClick: (ShopItem) -> Unit,
) {
ElevatedCard(
modifier = Modifier
.fillMaxWidth()
.height(178.dp),
onClick = { onItemClick(shopItem) },
shape = MaterialTheme.shapes.large,
elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
) {
Column(modifier = Modifier.fillMaxSize()) {
Box(
modifier = Modifier
.fillMaxWidth()
.weight(1f)
.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
contentAlignment = Alignment.Center
) {
if (shopItem.imageUrl.isNotBlank()) {
AsyncImage(
model = shopItem.imageUrl,
contentDescription = shopItem.title,
modifier = Modifier.fillMaxSize(),
contentScale = ContentScale.Crop
)
} else {
Icon(
imageVector = Icons.Outlined.ShoppingBag,
contentDescription = null,
modifier = Modifier.size(48.dp),
tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
)
}

                // Price Tag Overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.small
                        )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = shopItem.price.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            painter = painterResource(R.drawable.coins),
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = Color.Unspecified
                        )
                    }
                }
            }
            
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = shopItem.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = shopItem.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Button(
                    onClick = { onItemClick(shopItem) },
                    modifier = Modifier.fillMaxWidth().height(32.dp),
                    shape = MaterialTheme.shapes.small,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = if (shopItem.isDonation) "Support" else "Redeem",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
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
package com.gdsc.recyclr.screens.shop

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.gdsc.recyclr.R
import com.gdsc.recyclr.domain.model.ProductReview
import com.gdsc.recyclr.domain.model.ShopItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsContent(
item: ShopItem,
userPoints: Int,
wishlisted: Boolean,
relatedItems: List<ShopItem>,
onShare: () -> Unit,
onToggleWishlist: () -> Unit,
onPickRelated: (ShopItem) -> Unit,
onConfirmRedeem: (ShopItem, quantity: Int) -> Unit,
) {
var showRedeemDialog by remember { mutableStateOf(false) }
var showZoom by remember { mutableStateOf(false) }
var descExpanded by remember { mutableStateOf(false) }
var quantity by remember { mutableIntStateOf(1) }
val maxQty = when {
item.stockQuantity != null && item.stockQuantity >= 0 -> item.stockQuantity.coerceAtLeast(1)
else -> 99
}
val unitPoints = item.price
val totalPoints = unitPoints * quantity
val canUseStepper = item.id != "seed_tree"
val insufficientPoints = userPoints < totalPoints
val balanceAfter = (userPoints - totalPoints).coerceAtLeast(0)

    val titleColor = MaterialTheme.colorScheme.onSurface
    val accent = MaterialTheme.colorScheme.primary
    val galleryBg = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.28f)
    val chipBg = MaterialTheme.colorScheme.surfaceVariant

    Column(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(galleryBg),
            ) {
                when {
                    item.id == "seed_tree" && item.galleryUrls.isEmpty() -> {
                        Image(
                            painter = painterResource(R.drawable.people_planting_a_tree),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .clickable { showZoom = true },
                            contentScale = ContentScale.Fit,
                        )
                    }
                    item.galleryUrls.isNotEmpty() -> {
                        AsyncImage(
                            model = item.galleryUrls.first(),
                            contentDescription = item.title,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { showZoom = true },
                            contentScale = ContentScale.Crop,
                        )
                    }
                    else -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { showZoom = true },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.recycle),
                                contentDescription = null,
                                modifier = Modifier.size(96.dp),
                                tint = accent.copy(alpha = 0.45f),
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                ) {
                    IconButton(onClick = onShare) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = titleColor)
                    }
                    IconButton(onClick = onToggleWishlist) {
                        Icon(
                            imageVector = if (wishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = if (wishlisted) MaterialTheme.colorScheme.error else titleColor,
                        )
                    }
                }
                if (item.galleryUrls.size > 1) {
                    Row(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        repeat(item.galleryUrls.size.coerceAtMost(5)) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (it == 0) accent
                                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
                                    ),
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = item.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor,
                )
                Spacer(Modifier.height(8.dp))
                Surface(color = chipBg, shape = RoundedCornerShape(16.dp)) {
                    Text(
                        text = categoryChipLabel(item.category),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(8.dp))
                RatingRow(rating = item.avgRating, reviewCount = item.reviewsCount)
                Spacer(Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$totalPoints",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = accent,
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.shop_points_suffix),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = accent,
                        modifier = Modifier.padding(bottom = 2.dp),
                    )
                }
                item.marketPrice?.let { mp ->
                    if (mp > unitPoints) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$mp",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textDecoration = TextDecoration.LineThrough,
                            )
                            Spacer(Modifier.width(8.dp))
                            val pct = ((1f - unitPoints.toFloat() / mp) * 100).toInt().coerceIn(0, 99)
                            Text(
                                text = stringResource(R.string.shop_you_save_pct, pct),
                                fontSize = 12.sp,
                                color = accent,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.shop_your_balance, userPoints),
                    color = if (insufficientPoints) Color(0xFFC62828) else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                )
                if (insufficientPoints) {
                    Text(
                        text = stringResource(R.string.shop_need_more_points, totalPoints - userPoints),
                        color = Color(0xFFC62828),
                        fontSize = 13.sp,
                    )
                }

                Spacer(Modifier.height(16.dp))
                if (item.verifiedPartner) {
                    Surface(color = galleryBg, shape = RoundedCornerShape(12.dp)) {
                        Text(
                            text = stringResource(R.string.product_verified_partner),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            fontSize = 12.sp,
                            color = accent,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }
                StockRow(item)
                if (item.shipsFrom.isNotBlank()) {
                    Text(
                        text = stringResource(R.string.product_ships_from, item.shipsFrom),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (item.deliverySummary.isNotBlank()) {
                    Text(
                        text = stringResource(R.string.product_delivery, item.deliverySummary),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(Modifier.height(20.dp))
                Text(
                    text = stringResource(R.string.shop_description_title),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = item.description,
                    maxLines = if (descExpanded) Int.MAX_VALUE else 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .animateContentSize()
                        .clickable { descExpanded = !descExpanded },
                )
                TextButton(onClick = { descExpanded = !descExpanded }) {
                    Text(if (descExpanded) stringResource(R.string.read_less) else stringResource(R.string.read_more))
                }

                Spacer(Modifier.height(16.dp))
                Text(
                    stringResource(R.string.product_key_details),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = titleColor,
                )
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailCard(
                        Modifier.weight(1f),
                        stringResource(R.string.product_detail_carbon),
                        "${item.carbonOffsetTonnes} t CO₂",
                    )
                    DetailCard(
                        Modifier.weight(1f),
                        stringResource(R.string.product_detail_location),
                        item.locationLabel.ifBlank { "—" },
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailCard(
                        Modifier.weight(1f),
                        stringResource(R.string.product_detail_season),
                        item.plantingSeason.ifBlank { "—" },
                    )
                    DetailCard(
                        Modifier.weight(1f),
                        stringResource(R.string.product_detail_gift),
                        item.giftOption.ifBlank { "—" },
                    )
                }

                if (canUseStepper) {
                    Spacer(Modifier.height(16.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(stringResource(R.string.product_quantity), fontWeight = FontWeight.Bold, color = titleColor)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextButton(
                                onClick = { quantity = (quantity - 1).coerceAtLeast(1) },
                                enabled = quantity > 1,
                            ) { Text("−") }
                            Text(quantity.toString(), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp))
                            TextButton(
                                onClick = { quantity = (quantity + 1).coerceAtMost(maxQty) },
                                enabled = quantity < maxQty,
                            ) { Text("+") }
                        }
                    }
                    Text(
                        stringResource(R.string.product_total_points, totalPoints),
                        fontWeight = FontWeight.Bold,
                        color = accent,
                    )
                }

                Spacer(Modifier.height(24.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.product_customer_reviews),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleColor,
                    )
                    TextButton(onClick = { }) {
                        Text(stringResource(R.string.product_see_all))
                    }
                }
                val preview = item.reviewPreviews.firstOrNull()
                if (preview != null) {
                    ReviewPreviewRow(preview)
                } else {
                    ReviewPreviewRow(
                        ProductReview(
                            id = "demo",
                            productId = item.id,
                            authorName = "Aline N.",
                            rating = 5f,
                            comment = stringResource(R.string.product_review_sample),
                            createdAtMillis = System.currentTimeMillis(),
                        ),
                    )
                }

                Spacer(Modifier.height(24.dp))
                Text(
                    stringResource(R.string.product_recently_viewed),
                    fontWeight = FontWeight.Bold,
                    color = titleColor,
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    relatedItems.forEach { rel ->
                        Card(
                            modifier = Modifier
                                .width(120.dp)
                                .height(148.dp),
                            onClick = { onPickRelated(rel) },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            ),
                        ) {
                            Column(Modifier.fillMaxSize().padding(8.dp)) {
                                Text(
                                    rel.title,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 16.sp,
                                )
                                Spacer(Modifier.weight(1f))
                                Text(
                                    "${rel.price} pts",
                                    color = accent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }

        HorizontalDivider()
        Surface(
            tonalElevation = 2.dp,
            shadowElevation = 6.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.navigationBarsPadding()
        ) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Button(
                    onClick = { showRedeemDialog = true },
                    enabled = !insufficientPoints,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accent,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                ) {
                    Text(
                        text = if (item.isDonation) "Support this cause" else stringResource(R.string.redeem),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onToggleWishlist,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline,
                    ),
                ) {
                    Icon(
                        imageVector = if (wishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(if (wishlisted) "In Wishlist" else stringResource(R.string.product_add_wishlist))
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }

    if (showRedeemDialog) {
        AlertDialog(
            onDismissRequest = { showRedeemDialog = false },
            title = { Text(stringResource(R.string.product_redeem_confirm_title)) },
            text = {
                Text(stringResource(R.string.product_redeem_confirm_body, totalPoints, item.title, balanceAfter))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRedeemDialog = false
                        onConfirmRedeem(item, quantity)
                    },
                ) {
                    Text(
                        stringResource(R.string.action_confirm),
                        color = accent,
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showRedeemDialog = false }) { Text(stringResource(R.string.scan_cancel)) }
            },
        )
    }

    if (showZoom) {
        Dialog(
            onDismissRequest = { showZoom = false },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable { showZoom = false },
                contentAlignment = Alignment.Center,
            ) {
                when {
                    item.id == "seed_tree" && item.galleryUrls.isEmpty() ->
                        Image(
                            painter = painterResource(R.drawable.people_planting_a_tree),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentScale = ContentScale.Fit,
                        )
                    item.galleryUrls.isNotEmpty() ->
                        AsyncImage(
                            model = item.galleryUrls.first(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit,
                        )
                }
            }
        }
    }
}

@Composable
private fun categoryChipLabel(category: String): String {
val emoji = when (category.lowercase()) {
"nature" -> "🌱"
"bags" -> "🛍️"
"education" -> "📚"
"home" -> "🏠"
else -> "♻️"
}
return "$emoji $category"
}

@Composable
private fun RatingRow(rating: Float, reviewCount: Int) {
Row(verticalAlignment = Alignment.CenterVertically) {
repeat(5) { idx ->
val filled = rating >= idx + 1 - 0.25f
Icon(
imageVector = if (filled) Icons.Default.Star else Icons.Outlined.StarBorder,
contentDescription = null,
tint = Color(0xFFFFC107),
modifier = Modifier.size(18.dp),
)
}
Spacer(Modifier.width(8.dp))
Text(
text = "($reviewCount ${stringResource(R.string.product_reviews_suffix)})",
style = MaterialTheme.typography.labelMedium,
color = MaterialTheme.colorScheme.primary,
modifier = Modifier.clickable { },
)
}
}

@Composable
private fun StockRow(item: ShopItem) {
val (dot, label) = when (item.stockLabel) {
"out_of_stock" -> Color(0xFFB71C1C) to stringResource(R.string.stock_out)
"limited" -> Color(0xFFF57C00) to stringResource(R.string.stock_limited)
else -> MaterialTheme.colorScheme.primary to stringResource(R.string.stock_in_stock)
}
Row(verticalAlignment = Alignment.CenterVertically) {
Box(Modifier.size(8.dp).clip(CircleShape).background(dot))
Spacer(Modifier.width(6.dp))
Text(
label,
fontSize = 13.sp,
fontWeight = FontWeight.Medium,
color = MaterialTheme.colorScheme.onSurface,
)
}
Spacer(Modifier.height(8.dp))
}

@Composable
private fun DetailCard(modifier: Modifier, title: String, value: String) {
Card(
modifier = modifier,
colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
) {
Column(Modifier.padding(12.dp)) {
Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
Spacer(Modifier.height(4.dp))
Text(
value,
style = MaterialTheme.typography.titleSmall,
fontWeight = FontWeight.Bold,
color = MaterialTheme.colorScheme.onSurface,
)
}
}
}

@Composable
private fun ReviewPreviewRow(review: ProductReview) {
Column {
Row {
repeat(5) {
Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
}
}
Text(
"\"${review.comment}\"",
style = MaterialTheme.typography.bodyMedium,
color = MaterialTheme.colorScheme.onSurface,
modifier = Modifier.padding(top = 4.dp),
)
Text(
"— ${review.authorName}",
style = MaterialTheme.typography.labelSmall,
color = MaterialTheme.colorScheme.outline,
)
}
}
