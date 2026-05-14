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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.composable.BasicTopBar
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
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
                onClick = {}
            )
            WalletActionButton(
                icon = Icons.Default.ArrowDownward,
                label = "Receive",
                modifier = Modifier.weight(1f),
                onClick = {}
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
            
            TransactionItem("Received from Scan", "+1.2 $REC", "rRecyclr...x82", "Just now")
            TransactionItem("Redeemed Item", "-2.0 $REC", "rShop...y21", "Yesterday")
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
