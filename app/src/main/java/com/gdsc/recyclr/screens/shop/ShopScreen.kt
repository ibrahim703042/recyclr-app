package com.gdsc.recyclr.screens.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.preferences.ThemeToggleIconButton
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.ShopItem
import com.gdsc.recyclr.ui.theme.LeafGreen
import com.gdsc.recyclr.ui.theme.recyclrScreenBackground

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

    val shopLoadError = (shopItemsResponse as? Response.Failure)?.e?.message
    LaunchedEffect(shopLoadError) {
        if (shopLoadError != null) {
            snackbarHostState.showSnackbar(shopLoadError)
        }
    }
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
            is Response.Loading -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (selectedItem == null) {
                            stringResource(R.string.shop_title)
                        } else {
                            stringResource(R.string.shop_product_review)
                        },
                        fontWeight = FontWeight.Bold,
                    )
                },
                actions = {
                    ThemeToggleIconButton()
                    if (selectedItem == null) {
                        IconButton(onClick = {}) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null)
                        }
                        IconButton(onClick = {}) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                        }
                    }
                },
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 0.dp,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(recyclrScreenBackground())
                .padding(paddingValues),
        ) {
            when (val item = selectedItem) {
                null -> {
                    when (shopItemsResponse) {
                        is Response.Loading -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = LeafGreen)
                            }
                        }
                        is Response.Failure -> {
                            Text(
                                text = shopItemsResponse.e.message ?: stringResource(R.string.shop_load_failed),
                                modifier = Modifier.padding(20.dp),
                            )
                        }
                        is Response.Success -> {
                            val shopItems = shopItemsResponse.data.orEmpty()
                            LazyVerticalGrid(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                columns = GridCells.Fixed(2),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                items(shopItems) { shopItem ->
                                    ShopItemCard(
                                        shopItem = shopItem,
                                        onRedeem = { selectedItem = it },
                                    )
                                }
                            }
                        }
                    }
                }
                else -> ProductReviewContent(
                    shopItem = item,
                    onBack = { selectedItem = null },
                    onRedeem = { viewModel.redeem(item) },
                )
            }
        }
    }

    if (showConfirmation) {
        Dialog(onDismissRequest = { showConfirmation = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                backgroundColor = Color.White,
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(LeafGreen),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = stringResource(R.string.shop_confirmation_title), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = stringResource(R.string.shop_confirmation_message), fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            showConfirmation = false
                            selectedItem = null
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = LeafGreen),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = stringResource(R.string.shop_ok), color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductReviewContent(
    shopItem: ShopItem,
    onBack: () -> Unit,
    onRedeem: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
    ) {
        IconButton(onClick = onBack) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Card(
                modifier = Modifier.size(140.dp),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = Color.White,
                elevation = 0.dp,
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = shopItem.title.take(1), fontWeight = FontWeight.Bold, fontSize = 28.sp)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = shopItem.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "${shopItem.price} ${stringResource(R.string.coins)}", fontSize = 15.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onRedeem,
                    colors = ButtonDefaults.buttonColors(backgroundColor = LeafGreen),
                ) {
                    Text(text = stringResource(R.string.redeem), color = Color.White)
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = stringResource(R.string.shop_description), fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = shopItem.description.ifBlank {
                stringResource(R.string.shop_description_fallback)
            },
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
        )
    }
}
