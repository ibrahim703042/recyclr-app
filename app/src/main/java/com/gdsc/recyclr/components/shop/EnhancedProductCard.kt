package com.gdsc.recyclr.components.shop

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gdsc.recyclr.R
import com.gdsc.recyclr.domain.model.ShopItem

@Composable
fun EnhancedProductCard(
    item: ShopItem,
    onItemClick: () -> Unit,
    onWishlistToggle: () -> Unit,
    isWishlisted: Boolean,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        onClick = onItemClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
            ) {
                ShopItemImage(
                    item = item,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                )

                IconButton(
                    onClick = onWishlistToggle,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(32.dp)
                        .background(Color.White.copy(alpha = 0.92f), CircleShape),
                ) {
                    Icon(
                        imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isWishlisted) Color(0xFFE53935) else Color.Gray,
                        modifier = Modifier.size(18.dp),
                    )
                }

                val stockQty = item.stockQuantity ?: -1
                if (stockQty in 1..9) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp),
                        color = Color(0xFFFF5722),
                        shape = RoundedCornerShape(6.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.shop_stock_badge, stockQty),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.coins),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFFFFA726),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.shop_points_format, item.price),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Text(
                        text = stringResource(R.string.shop_redeem_short),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun ShopItemImage(item: ShopItem, modifier: Modifier = Modifier) {
    when {
        item.id == "seed_tree" -> {
            Image(
                painter = painterResource(R.drawable.people_planting_a_tree),
                contentDescription = item.title,
                modifier = modifier.padding(8.dp),
                contentScale = ContentScale.Fit,
            )
        }
        item.imageUrl.isNotBlank() -> {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                modifier = modifier,
                contentScale = ContentScale.Crop,
                error = painterResource(shopCategoryPlaceholder(item.category)),
            )
        }
        else -> {
            Image(
                painter = painterResource(shopCategoryPlaceholder(item.category)),
                contentDescription = item.title,
                modifier = modifier.padding(12.dp),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

private fun shopCategoryPlaceholder(category: String): Int = when (category.lowercase()) {
    "nature" -> R.drawable.people_planting_a_tree
    "bags" -> R.drawable.recycle
    "education" -> R.drawable.recycle
    "home" -> R.drawable.recycle
    else -> R.drawable.recycle
}
