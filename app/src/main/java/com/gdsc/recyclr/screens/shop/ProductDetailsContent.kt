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

private val TitleColor = Color(0xFF1F2A1B)
private val BrandGreen = Color(0xFF2E7D32)
private val GalleryBg = Color(0xFFE8F5E9)
private val ChipBg = Color(0xFFEEEEEE)

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
                    .background(GalleryBg),
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
                                tint = BrandGreen.copy(alpha = 0.35f),
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
                        Icon(Icons.Default.Share, contentDescription = null, tint = TitleColor)
                    }
                    IconButton(onClick = onToggleWishlist) {
                        Icon(
                            imageVector = if (wishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = if (wishlisted) Color(0xFFC62828) else TitleColor,
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
                                    .background(if (it == 0) BrandGreen else Color.White.copy(alpha = 0.6f)),
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
                    color = TitleColor,
                )
                Spacer(Modifier.height(8.dp))
                Surface(color = ChipBg, shape = RoundedCornerShape(16.dp)) {
                    Text(
                        text = categoryChipLabel(item.category),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 12.sp,
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
                        color = BrandGreen,
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.shop_points_suffix),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandGreen,
                        modifier = Modifier.padding(bottom = 2.dp),
                    )
                }
                item.marketPrice?.let { mp ->
                    if (mp > unitPoints) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$mp",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                textDecoration = TextDecoration.LineThrough,
                            )
                            Spacer(Modifier.width(8.dp))
                            val pct = ((1f - unitPoints.toFloat() / mp) * 100).toInt().coerceIn(0, 99)
                            Text(
                                text = stringResource(R.string.shop_you_save_pct, pct),
                                fontSize = 12.sp,
                                color = BrandGreen,
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
                    Surface(color = GalleryBg, shape = RoundedCornerShape(12.dp)) {
                        Text(
                            text = stringResource(R.string.product_verified_partner),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            fontSize = 12.sp,
                            color = BrandGreen,
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
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = item.description,
                    maxLines = if (descExpanded) Int.MAX_VALUE else 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .animateContentSize()
                        .clickable { descExpanded = !descExpanded },
                )
                TextButton(onClick = { descExpanded = !descExpanded }) {
                    Text(if (descExpanded) stringResource(R.string.read_less) else stringResource(R.string.read_more))
                }

                Spacer(Modifier.height(16.dp))
                Text(stringResource(R.string.product_key_details), fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                        Text(stringResource(R.string.product_quantity), fontWeight = FontWeight.Bold)
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
                        color = BrandGreen,
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
                Text(stringResource(R.string.product_recently_viewed), fontWeight = FontWeight.Bold)
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
                                .height(140.dp),
                            onClick = { onPickRelated(rel) },
                        ) {
                            Column(Modifier.padding(8.dp)) {
                                Text(rel.title, maxLines = 2, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelMedium)
                                Spacer(Modifier.weight(1f))
                                Text("${rel.price} pts", color = BrandGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }

        HorizontalDivider()
        Surface(tonalElevation = 2.dp, shadowElevation = 6.dp) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Button(
                    onClick = { showRedeemDialog = true },
                    enabled = !insufficientPoints,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen, contentColor = Color.White),
                ) {
                    Text(stringResource(R.string.redeem), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onToggleWishlist,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(28.dp),
                ) {
                    Text(stringResource(R.string.product_add_wishlist))
                }
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
                ) { Text(stringResource(R.string.action_confirm), color = BrandGreen, fontWeight = FontWeight.Bold) }
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
        else -> BrandGreen to stringResource(R.string.stock_in_stock)
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(dot))
        Spacer(Modifier.width(6.dp))
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium)
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
            Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
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
            modifier = Modifier.padding(top = 4.dp),
        )
        Text(
            "— ${review.authorName}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
        )
    }
}
