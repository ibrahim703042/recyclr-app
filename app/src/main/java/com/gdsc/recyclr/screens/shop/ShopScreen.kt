package com.gdsc.recyclr.screens.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gdsc.recyclr.components.composable.BasicTopBar
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.ShopItem
import com.gdsc.recyclr.ui.theme.Gray_color
import com.gdsc.recyclr.ui.theme.RecyclrTheme

@Composable
fun ShopScreen(
    viewModel: ShopViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val shopItemsResponse = viewModel.shopItemsResponse
    val redemptionHistoryResponse = viewModel.redemptionHistoryResponse
    val redeemResponse = viewModel.redeemResponse
    var pendingRedeem by remember { mutableStateOf<ShopItem?>(null) }

    val shopLoadError = (shopItemsResponse as? Response.Failure)?.e?.message
    LaunchedEffect(shopLoadError) {
        if (shopLoadError != null) {
            snackbarHostState.showSnackbar(shopLoadError)
        }
    }
    val historyLoadError = (redemptionHistoryResponse as? Response.Failure)?.e?.message
    LaunchedEffect(historyLoadError) {
        if (historyLoadError != null) {
            snackbarHostState.showSnackbar("Historique : $historyLoadError")
        }
    }
    LaunchedEffect(redeemResponse) {
        when (redeemResponse) {
            is Response.Failure -> {
                snackbarHostState.showSnackbar(redeemResponse.e.message ?: "Échec de l'échange")
                viewModel.acknowledgeRedeemFeedback()
            }
            is Response.Success -> {
                if (redeemResponse.data != null) {
                    snackbarHostState.showSnackbar("Échange enregistré")
                    viewModel.acknowledgeRedeemFeedback()
                }
            }
            is Response.Loading -> Unit
        }
    }
    
    Scaffold(
        topBar = {
            BasicTopBar(title = "Shop")
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .background(color = Gray_color)
                .padding(paddingValues)
                .padding(20.dp)
                .fillMaxSize()
        ) {
            when (shopItemsResponse) {
                is Response.Loading -> CircularProgressIndicator()
                is Response.Failure -> Text(text = shopItemsResponse.e.message ?: "Échec du chargement")
                is Response.Success -> {
                    val shopItems = shopItemsResponse.data.orEmpty()
                    LazyVerticalGrid(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        columns = GridCells.Adaptive(150.dp),
                        content = {
                            items(items = shopItems) { shopItem ->
                                ShopItemCard(shopItem = shopItem, onRedeem = { pendingRedeem = it })
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Redemption history", style = MaterialTheme.typography.h6)
                    when (redemptionHistoryResponse) {
                        is Response.Loading -> Text("Loading...")
                        is Response.Failure -> Text(redemptionHistoryResponse.e.message ?: "Historique indisponible")
                        is Response.Success -> {
                            val history = redemptionHistoryResponse.data.orEmpty()
                            if (history.isEmpty()) {
                                Text("No redemptions yet.")
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    history.take(5).forEach { r ->
                                        Text(text = "${r.shopItemTitle} (-${r.pointsCost})")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    val toRedeem = pendingRedeem
    if (toRedeem != null) {
        AlertDialog(
            onDismissRequest = { pendingRedeem = null },
            title = { Text("Confirm redemption") },
            text = { Text("Redeem \"${toRedeem.title}\" for ${toRedeem.price} points?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.redeem(toRedeem)
                        pendingRedeem = null
                    }
                ) { Text("Redeem") }
            },
            dismissButton = {
                OutlinedButton(onClick = { pendingRedeem = null }) { Text("Cancel") }
            }
        )
    }

}

@Preview(showBackground = true)
@Composable
private fun PreviewShopScreen() {
    RecyclrTheme {
        ShopScreen()
    }
}