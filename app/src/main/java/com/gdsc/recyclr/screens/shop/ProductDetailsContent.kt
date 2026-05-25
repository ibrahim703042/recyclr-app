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
import com.gdsc.recyclr.components.shop.*
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
    val scrollState = rememberScrollState()

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = ProductCheckoutFooterHeight + 8.dp),
        ) {
            // Enhanced Image Gallery with Pager
            if (item.galleryUrls.isNotEmpty()) {
                Box {
                    EnhancedImageGallery(
                        images = item.galleryUrls,
                        onImageClick = { showZoom = true },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // Action buttons overlay
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
                }
            } else {
                // Fallback for items without gallery
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(galleryBg),
                ) {
                    when {
                        item.id == "seed_tree" -> {
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
                Spacer(Modifier.height(12.dp))
                ProductInlinePriceRow(
                    unitPoints = unitPoints,
                    marketPrice = item.marketPrice,
                )
                Spacer(Modifier.height(12.dp))
                
                // Delivery Estimate Card
                DeliveryEstimateCard(
                    deliveryDays = 3,
                    isFastDelivery = item.verifiedPartner
                )
                
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
                        
                        // Enhanced Quantity Selector
                        EnhancedQuantitySelector(
                            quantity = quantity,
                            maxQuantity = maxQty,
                            onQuantityChange = { quantity = it }
                        )
                    }
                    Spacer(Modifier.height(8.dp))
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
                
                // Customers Also Bought Section
                if (relatedItems.isNotEmpty()) {
                    CustomersAlsoBoughtSection(
                        items = relatedItems,
                        onItemClick = onPickRelated
                    )
                    Spacer(Modifier.height(24.dp))
                }
                
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
                Spacer(Modifier.height(16.dp))
            }
        }

        ShopStickyCheckoutBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            totalPoints = totalPoints,
            actionLabel = if (item.isDonation) {
                stringResource(R.string.shop_support_cause)
            } else {
                stringResource(R.string.redeem)
            },
            enabled = !insufficientPoints,
            isLoading = false,
            onAction = { showRedeemDialog = true },
            userBalance = userPoints,
            showInsufficientHint = insufficientPoints,
            pointsNeeded = (totalPoints - userPoints).coerceAtLeast(0),
        )
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
private fun ProductInlinePriceRow(
    unitPoints: Int,
    marketPrice: Int?,
) {
    val accent = MaterialTheme.colorScheme.primary
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
            shape = RoundedCornerShape(12.dp),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.coins),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color(0xFFFFA726),
                )
                Text(
                    text = stringResource(R.string.shop_points_format, unitPoints),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = accent,
                )
            }
        }
        marketPrice?.let { mp ->
            if (mp > unitPoints) {
                val pct = ((1f - unitPoints.toFloat() / mp) * 100).toInt().coerceIn(0, 99)
                Text(
                    text = stringResource(R.string.shop_you_save_pct, pct),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
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
