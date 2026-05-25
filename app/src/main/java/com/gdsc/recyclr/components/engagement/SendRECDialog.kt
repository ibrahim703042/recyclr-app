package com.gdsc.recyclr.components.engagement

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gdsc.recyclr.components.design.RecyclrDialogSurface

@Composable
fun SendRECDialog(
    availableREC: Float,
    onDismiss: () -> Unit,
    onConfirm: (String, Float, String) -> Unit,
    isLoading: Boolean = false
) {
    var recipientAddress by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var showPreview by remember { mutableStateOf(false) }
    
    val amountFloat = amount.toFloatOrNull() ?: 0f
    val isValidAmount = amountFloat > 0 && amountFloat <= availableREC
    val isValidAddress = recipientAddress.startsWith("r") && 
                        recipientAddress.length in 25..35 && 
                        recipientAddress.all { it.isLetterOrDigit() }
    val canProceed = isValidAmount && isValidAddress

    RecyclrDialogSurface(
        onDismiss = onDismiss,
        dismissEnabled = !isLoading,
    ) {
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
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "Send REC",
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
                
                // Recipient Address Input
                OutlinedTextField(
                    value = recipientAddress,
                    onValueChange = { 
                        recipientAddress = it.trim()
                        showPreview = it.startsWith("r") && amount.toFloatOrNull()?.let { amt -> amt > 0 } ?: false
                    },
                    label = { Text("Recipient Address") },
                    placeholder = { Text("rXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    isError = recipientAddress.isNotEmpty() && !isValidAddress,
                    supportingText = {
                        if (recipientAddress.isNotEmpty() && !isValidAddress) {
                            Text(
                                text = "Invalid XRPL address format",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Amount Input
                OutlinedTextField(
                    value = amount,
                    onValueChange = { 
                        amount = it
                        showPreview = recipientAddress.startsWith("r") && (it.toFloatOrNull() ?: 0f) > 0
                    },
                    label = { Text("Amount (REC)") },
                    placeholder = { Text("0.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    isError = amount.isNotEmpty() && !isValidAmount,
                    supportingText = {
                        if (amount.isNotEmpty() && !isValidAmount) {
                            Text(
                                text = if (amountFloat > availableREC) 
                                    "Insufficient balance" 
                                else 
                                    "Enter a valid amount",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    trailingIcon = {
                        TextButton(
                            onClick = { 
                                amount = availableREC.toString()
                                showPreview = recipientAddress.startsWith("r")
                            },
                            enabled = !isLoading
                        ) {
                            Text("MAX")
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Note/Memo Input (Optional)
                OutlinedTextField(
                    value = note,
                    onValueChange = { if (it.length <= 100) note = it },
                    label = { Text("Note (Optional)") },
                    placeholder = { Text("Add a message...") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    supportingText = {
                        Text(
                            text = "${note.length}/100",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    maxLines = 2
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Transaction Preview
                AnimatedVisibility(
                    visible = showPreview && canProceed,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Transaction Preview",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Divider(modifier = Modifier.padding(vertical = 4.dp))
                            
                            // To
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "To",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${recipientAddress.take(8)}...${recipientAddress.takeLast(6)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            
                            // Amount
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Amount",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${String.format("%.2f", amountFloat)} REC",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            
                            // Remaining Balance
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Remaining Balance",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${String.format("%.2f", availableREC - amountFloat)} REC",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
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
                        onClick = { onConfirm(recipientAddress, amountFloat, note) },
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
                            Text("Send")
                        }
                    }
                }
    }
}
