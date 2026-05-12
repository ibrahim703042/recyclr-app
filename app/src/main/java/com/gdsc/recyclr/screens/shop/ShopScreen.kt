package com.gdsc.recyclr.screens.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.composable.RecyclrTopBar
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.ShopItem
import com.gdsc.recyclr.screens.engagement.DonationHubSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    viewModel: ShopViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val shopItemsResponse = viewModel.shopItemsResponse
    val redeemResponse = viewModel.redeemResponse
    
    var selectedItem by remember { mutableStateOf<ShopItem?>(null) }
    var showConfirmation by remember { mutableStateOf(false) }
    var showDonations by remember { mutableStateOf(false) }

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

    Scaffold(
        topBar = {
            RecyclrTopBar(
                title = if (selectedItem == null) "Marketplace" else "Product Details",
                onNavigationClick = if (selectedItem != null) { { selectedItem = null } } else null,
                actions = {
                    if (selectedItem == null) {
                        IconButton(onClick = { /* TODO */ }) {
                            Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            if (selectedItem == null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Category Selection Tabs
                    TabRow(
                        selectedTabIndex = if (showDonations) 1 else 0,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                        divider = {}
                    ) {
                        Tab(
                            selected = !showDonations,
                            onClick = { showDonations = false },
                            text = { Text("Shop Items", style = MaterialTheme.typography.titleSmall) }
                        )
                        Tab(
                            selected = showDonations,
                            onClick = { showDonations = true },
                            text = { Text("Donations", style = MaterialTheme.typography.titleSmall) }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        if (showDonations) {
                            Box(modifier = Modifier.padding(16.dp).widthIn(max = 800.dp).align(Alignment.TopCenter)) {
                                DonationHubSection()
                            }
                        } else {
                            when (shopItemsResponse) {
                                is Response.Loading -> {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator()
                                    }
                                }
                                is Response.Failure -> {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text(text = "Failed to load marketplace items.")
                                    }
                                }
                                is Response.Success -> {
                                    val shopItems = shopItemsResponse.data.orEmpty()
                                    LazyVerticalGrid(
                                        columns = GridCells.Fixed(2),
                                        modifier = Modifier.fillMaxSize().widthIn(max = 1200.dp).align(Alignment.TopCenter),
                                        contentPadding = PaddingValues(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        items(shopItems) { item ->
                                            ShopItemCard(
                                                shopItem = item,
                                                onItemClick = { selectedItem = it }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                ProductDetailsView(
                    item = selectedItem!!,
                    onRedeem = { viewModel.redeem(selectedItem!!) }
                )
            }
        }
    }

    if (showConfirmation) {
        AlertDialog(
            onDismissRequest = { showConfirmation = false },
            confirmButton = {
                Button(
                    onClick = { 
                        showConfirmation = false 
                        selectedItem = null
                    },
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Done")
                }
            },
            title = { Text("Redemption Successful!") },
            text = { Text("Your item has been redeemed. You can find the voucher code in your profile wallet.") },
            icon = {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun ProductDetailsView(
    item: ShopItem,
    onRedeem: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.widthIn(max = 800.dp).fillMaxWidth()) {
            // Hero Image Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surface)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (item.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingBag,
                        contentDescription = null,
                        modifier = Modifier.size(120.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = item.category,
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = item.price.toString(),
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Icon(
                                painter = painterResource(R.drawable.coins),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.Unspecified
                            )
                        }
                        Text(
                            text = "Market Price",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                
                // Features Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ProductFeatureItem(
                        icon = Icons.Outlined.VerifiedUser,
                        label = "Verified",
                        modifier = Modifier.weight(1f)
                    )
                    ProductFeatureItem(
                        icon = Icons.Outlined.Inventory2,
                        label = "In Stock",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = "Product Description",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 26.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                item.termsSummary?.let { terms ->
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Terms & Conditions",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = terms,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))
                
                Button(
                    onClick = onRedeem,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Redeem Now",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ProductFeatureItem(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, style = MaterialTheme.typography.labelMedium)
        }
    }
}
