package com.gdsc.recyclr.components.engagement

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gdsc.recyclr.domain.model.engagement.Currency
import com.gdsc.recyclr.domain.model.engagement.TransactionStatus
import com.gdsc.recyclr.domain.model.engagement.TransactionType
import com.gdsc.recyclr.domain.model.engagement.WalletTransaction
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

@Composable
fun TransactionHistoryCard(
    transaction: WalletTransaction,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Main Transaction Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon and Type
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Transaction Icon
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = getTransactionColor(transaction.type).copy(alpha = 0.2f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = getTransactionIcon(transaction.type),
                                contentDescription = null,
                                tint = getTransactionColor(transaction.type),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    // Type and Description
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = getTransactionTypeLabel(transaction.type),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = transaction.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Amount and Status
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = formatAmount(transaction),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = getAmountColor(transaction.type)
                    )
                    
                    // Status Badge
                    TransactionStatusBadge(status = transaction.status)
                }
            }
            
            // Timestamp
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formatTimestamp(transaction.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
            
            // Expanded Details
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Divider()
                    
                    // Transaction ID
                    DetailRow(
                        label = "Transaction ID",
                        value = transaction.id,
                        isCopyable = true,
                        context = context
                    )
                    
                    // From Address (if applicable)
                    if (transaction.fromAddress.isNotBlank()) {
                        DetailRow(
                            label = "From",
                            value = transaction.fromAddress,
                            isCopyable = true,
                            context = context
                        )
                    }
                    
                    // To Address (if applicable)
                    if (transaction.toAddress.isNotBlank()) {
                        DetailRow(
                            label = "To",
                            value = transaction.toAddress,
                            isCopyable = true,
                            context = context
                        )
                    }
                    
                    // Metadata (if any)
                    if (transaction.metadata.isNotEmpty()) {
                        transaction.metadata.forEach { (key, value) ->
                            DetailRow(
                                label = formatMetadataKey(key),
                                value = value,
                                isCopyable = false,
                                context = context
                            )
                        }
                    }
                    
                    // Full Timestamp
                    DetailRow(
                        label = "Date & Time",
                        value = formatFullTimestamp(transaction.timestamp),
                        isCopyable = false,
                        context = context
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionStatusBadge(status: TransactionStatus) {
    val (color, label) = when (status) {
        TransactionStatus.PENDING -> MaterialTheme.colorScheme.tertiary to "Pending"
        TransactionStatus.COMPLETED -> Color(0xFF4CAF50) to "Completed"
        TransactionStatus.FAILED -> MaterialTheme.colorScheme.error to "Failed"
        TransactionStatus.CANCELLED -> MaterialTheme.colorScheme.outline to "Cancelled"
    }
    
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.2f)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    isCopyable: Boolean,
    context: Context
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (value.length > 20) "${value.take(10)}...${value.takeLast(8)}" else value,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = if (isCopyable) FontFamily.Monospace else FontFamily.Default,
                fontWeight = FontWeight.Medium
            )
            
            if (isCopyable) {
                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText(label, value)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "$label copied!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

private fun getTransactionIcon(type: TransactionType): ImageVector {
    return when (type) {
        TransactionType.SCAN_REWARD -> Icons.Default.Add
        TransactionType.SHOP_REDEMPTION -> Icons.Default.ShoppingCart
        TransactionType.REC_SEND -> Icons.Default.Send
        TransactionType.REC_RECEIVE -> Icons.Default.CallReceived
        TransactionType.REC_WITHDRAWAL -> Icons.Default.AccountBalanceWallet
        TransactionType.CARBON_SALE -> Icons.Default.TrendingUp
        TransactionType.DONATION -> Icons.Default.Star
    }
}

private fun getTransactionColor(type: TransactionType): Color {
    return when (type) {
        TransactionType.SCAN_REWARD, TransactionType.REC_RECEIVE -> Color(0xFF4CAF50)
        TransactionType.SHOP_REDEMPTION, TransactionType.REC_SEND, TransactionType.REC_WITHDRAWAL, TransactionType.CARBON_SALE, TransactionType.DONATION -> Color(0xFFFF9800)
    }
}

private fun getAmountColor(type: TransactionType): Color {
    return when (type) {
        TransactionType.SCAN_REWARD, TransactionType.REC_RECEIVE -> Color(0xFF4CAF50)
        TransactionType.SHOP_REDEMPTION, TransactionType.REC_SEND, TransactionType.REC_WITHDRAWAL, TransactionType.CARBON_SALE, TransactionType.DONATION -> Color(0xFFF44336)
    }
}

private fun getTransactionTypeLabel(type: TransactionType): String {
    return when (type) {
        TransactionType.SCAN_REWARD -> "Scan Reward"
        TransactionType.SHOP_REDEMPTION -> "Shop Redemption"
        TransactionType.REC_SEND -> "REC Sent"
        TransactionType.REC_RECEIVE -> "REC Received"
        TransactionType.REC_WITHDRAWAL -> "REC Withdrawal"
        TransactionType.CARBON_SALE -> "Carbon Credits Sold"
        TransactionType.DONATION -> "Donation"
    }
}

private fun formatAmount(transaction: WalletTransaction): String {
    val sign = when (transaction.type) {
        TransactionType.SCAN_REWARD, TransactionType.REC_RECEIVE -> "+"
        else -> "-"
    }
    
    val suffix = when (transaction.currency) {
        Currency.POINTS -> " pts"
        Currency.REC -> " REC"
        Currency.CARBON_CREDITS -> " tCO₂"
    }
    
    return "$sign${String.format("%.2f", abs(transaction.amount))}$suffix"
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60_000 -> "Just now"
        diff < 3600_000 -> "${diff / 60_000} minutes ago"
        diff < 86400_000 -> "${diff / 3600_000} hours ago"
        diff < 604800_000 -> "${diff / 86400_000} days ago"
        else -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))
    }
}

private fun formatFullTimestamp(timestamp: Long): String {
    return SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault()).format(Date(timestamp))
}

private fun formatMetadataKey(key: String): String {
    return key.split("(?=[A-Z])".toRegex())
        .joinToString(" ") { it.capitalize(Locale.getDefault()) }
}
