package com.gdsc.recyclr.components.engagement

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gdsc.recyclr.components.design.RecyclrDialogSurface

@Composable
fun WithdrawRECDialog(
    availableREC: Float,
    onDismiss: () -> Unit,
    onConfirm: (Float, String) -> Unit,
    isLoading: Boolean = false
) {
    var amount by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var showWarning by remember { mutableStateOf(false) }
    
    val amountFloat = amount.toFloatOrNull() ?: 0f
    val isValidAmount = amountFloat >= 10f && amountFloat <= availableREC
    val isValidAddress = address.startsWith("r") && address.length in 25..35 && address.all { it.isLetterOrDigit() }
    val canProceed = isValidAmount && isValidAddress

    RecyclrDialogSurface(onDismiss = onDismiss, dismissEnabled = !isLoading) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccountBalanceWallet,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "Withdraw REC",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    if (!isLoading) {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close"
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Available Balance
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Available Balance",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${String.format("%.2f", availableREC)} REC",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Amount Input
                OutlinedTextField(
                    value = amount,
                    onValueChange = { 
                        amount = it
                        showWarning = (it.toFloatOrNull() ?: 0f) >= 10f
                    },
                    label = { Text("Amount (REC)") },
                    placeholder = { Text("Minimum 10 REC") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    isError = amount.isNotEmpty() && !isValidAmount,
                    supportingText = {
                        if (amount.isNotEmpty() && !isValidAmount) {
                            Text(
                                text = when {
                                    amountFloat < 10f -> "Minimum withdrawal is 10 REC"
                                    amountFloat > availableREC -> "Insufficient balance"
                                    else -> "Enter a valid amount"
                                },
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    trailingIcon = {
                        TextButton(
                            onClick = { 
                                amount = availableREC.toString()
                                showWarning = true
                            },
                            enabled = !isLoading
                        ) {
                            Text("MAX")
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Wallet Address Input
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it.trim() },
                    label = { Text("XRPL Wallet Address") },
                    placeholder = { Text("rXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    isError = address.isNotEmpty() && !isValidAddress,
                    supportingText = {
                        if (address.isNotEmpty() && !isValidAddress) {
                            Text(
                                text = "Invalid XRPL address format",
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Text(
                                text = "Must start with 'r' and be 25-35 characters",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Warning Card
                AnimatedVisibility(
                    visible = showWarning && isValidAmount,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(24.dp)
                            )
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Important Notice",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = "• This operation is irreversible\n• Double-check the wallet address\n• Processing time: 2-5 minutes\n• Network fees may apply",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Estimated Completion Time
                if (canProceed) {
                    Text(
                        text = "Estimated completion: 2-5 minutes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = { onConfirm(amountFloat, address) },
                        modifier = Modifier.weight(1f),
                        enabled = canProceed && !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Withdraw")
                        }
                    }
                }
    }
}
