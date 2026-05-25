package com.gdsc.recyclr.screens.engagement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.composable.BasicTopBar
import com.gdsc.recyclr.components.design.RecyclrLayout
import com.gdsc.recyclr.components.design.RecyclrWidthContainer
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.engagement.RecWallet

@Composable
fun BlockchainWalletScreen(onBack: () -> Unit, viewModel: EngagementViewModel = hiltViewModel()) {
    Scaffold(
        topBar = {
            BasicTopBar(title = stringResource(R.string.blockchain_wallet_title), onBack = onBack)
        }
    ) { padding ->
        when (val response = viewModel.walletResponse) {
            is Response.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            is Response.Failure -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(response.e.message ?: "Error") }
            is Response.Success -> response.data?.let { BlockchainWalletBody(it, padding) }
        }
    }
}

@Composable
private fun BlockchainWalletBody(wallet: RecWallet, padding: PaddingValues) {
    val viewModel: EngagementViewModel = hiltViewModel()
    var showSendDialog by remember { mutableStateOf(false) }
    var showReceiveDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    // Handle send operation state
    LaunchedEffect(viewModel.sendOperationState) {
        when (val state = viewModel.sendOperationState) {
            is OperationState.Success -> {
                android.widget.Toast.makeText(context, state.message, android.widget.Toast.LENGTH_LONG).show()
                showSendDialog = false
                viewModel.resetOperationState("send")
            }
            is OperationState.Error -> {
                android.widget.Toast.makeText(context, state.message, android.widget.Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        RecyclrWidthContainer(horizontalPadding = RecyclrLayout.ScreenPadding) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
        // Token Balance
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "${wallet.recBalance} $REC", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Black)
            Text(text = "≈ $${String.format("%.2f", wallet.recBalance * 0.64)} USD", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        
        // Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WalletActionButton(
                icon = Icons.Default.ArrowUpward,
                label = "Send",
                modifier = Modifier.weight(1f),
                onClick = { showSendDialog = true }
            )
            WalletActionButton(
                icon = Icons.Default.ArrowDownward,
                label = "Receive",
                modifier = Modifier.weight(1f),
                onClick = { showReceiveDialog = true }
            )
        }
        
        // Security Notice
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Security Reminder", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    Text("Backup your seed phrase. Recyclr does not store it.", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
        
        // Transaction History
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Transaction History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                TextButton(onClick = {}) { Text("View All") }
            }
            
            // Real transaction history from ViewModel
            when (val response = viewModel.transactionHistoryResponse) {
                is Response.Loading -> {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        repeat(3) {
                            com.gdsc.recyclr.components.common.ShimmerEffect(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                            )
                        }
                    }
                }
                is Response.Failure -> {
                    com.gdsc.recyclr.components.common.ErrorState(
                        message = response.e.message ?: "Failed to load transactions",
                        onRetry = { viewModel.loadTransactionHistory() }
                    )
                }
                is Response.Success -> {
                    val transactions = response.data.orEmpty()
                    if (transactions.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No transactions yet",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        transactions.take(5).forEach { transaction ->
                            com.gdsc.recyclr.components.engagement.TransactionHistoryCard(
                                transaction = transaction
                            )
                        }
                    }
                }
            }
        }
        
        // Connect XUMM
        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Icon(imageVector = Icons.Default.Link, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Connect XUMM Wallet")
        }
            }
        }
    }
    
    // Dialogs
    if (showSendDialog) {
        com.gdsc.recyclr.components.engagement.SendRECDialog(
            availableREC = wallet.recBalance.toFloat(),
            onDismiss = { 
                showSendDialog = false
                viewModel.resetOperationState("send")
            },
            onConfirm = { toAddress, amount, note ->
                viewModel.sendREC(toAddress, amount, note)
            },
            isLoading = viewModel.sendOperationState is OperationState.Loading
        )
    }
    
    if (showReceiveDialog) {
        com.gdsc.recyclr.components.engagement.ReceiveRECDialog(
            walletAddress = wallet.address,
            onDismiss = { showReceiveDialog = false }
        )
    }
}

@Composable
private fun WalletActionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primaryContainer,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TransactionItem(title: String, amount: String, address: String, time: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(text = address, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = amount, 
                    style = MaterialTheme.typography.titleMedium, 
                    fontWeight = FontWeight.Black,
                    color = if (amount.startsWith("+")) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                )
                Text(text = time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

private const val REC = "REC"
