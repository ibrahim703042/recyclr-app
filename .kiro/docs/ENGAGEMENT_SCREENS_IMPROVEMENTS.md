# Engagement Screens Improvements

## 🎯 Overview

This document outlines comprehensive improvements for the engagement screens to match the quality of other integrated components.

---

## 🔧 Key Improvements Needed

### 1. Loading States
**Current**: Basic `CircularProgressIndicator`
**Improved**: Use `ShimmerEffect` component

### 2. Error States
**Current**: Plain `Text` with error message
**Improved**: Use `ErrorState` component with retry

### 3. Empty States
**Current**: No empty state handling
**Improved**: Add friendly empty state messages

### 4. Animations
**Current**: No animations
**Improved**: Add fade-in, slide-in animations

### 5. Sell & Withdrawal
**Current**: Buttons with no functionality
**Improved**: Add dialogs and confirmation flows

---

## 📝 Screen-by-Screen Improvements

### 1. Challenge Screen

**Add Shimmer Loading**:
```kotlin
is Response.Loading -> {
    ShimmerEffect(
        type = ShimmerEffect.ShimmerType.CARD,
        modifier = Modifier.fillMaxWidth().height(300.dp)
    )
}
```

**Add Error State**:
```kotlin
is Response.Failure -> {
    ErrorState(
        message = response.e.message ?: stringResource(R.string.error_generic),
        onRetry = { viewModel.refresh() },
        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp)
    )
}
```

**Add Animation**:
```kotlin
is Response.Success -> response.data?.let {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInVertically()
    ) {
        ChallengeBody(it)
    }
}
```

---

### 2. Leaderboard Screen

**Add Shimmer Loading**:
```kotlin
is Response.Loading -> {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(10) {
            ShimmerEffect(
                type = ShimmerEffect.ShimmerType.CARD,
                modifier = Modifier.fillMaxWidth().height(72.dp)
            )
        }
    }
}
```

**Add Empty State**:
```kotlin
if (response.data.orEmpty().isEmpty()) {
    ErrorState(
        message = "No leaderboard data yet",
        icon = Icons.Default.EmojiEvents,
        onRetry = null, // No retry for empty state
        modifier = Modifier.fillMaxSize()
    )
}
```

---

### 3. Wallet Screen

**Add Sell Dialog**:
```kotlin
var showSellDialog by remember { mutableStateOf(false) }
var sellAmount by remember { mutableStateOf("") }

if (showSellDialog) {
    AlertDialog(
        onDismissRequest = { showSellDialog = false },
        title = { Text("Sell Carbon Credits") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Available: ${wallet.carbonCreditsTonnes} tCO2")
                OutlinedTextField(
                    value = sellAmount,
                    onValueChange = { sellAmount = it },
                    label = { Text("Amount (tCO2)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                Text(
                    "Estimated value: $${String.format("%.2f", (sellAmount.toFloatOrNull() ?: 0f) * 10)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.sellCarbonCredits(sellAmount.toFloatOrNull() ?: 0f)
                    showSellDialog = false
                }
            ) {
                Text("Confirm Sale")
            }
        },
        dismissButton = {
            TextButton(onClick = { showSellDialog = false }) {
                Text("Cancel")
            }
        }
    )
}
```

**Add Withdrawal Dialog**:
```kotlin
var showWithdrawDialog by remember { mutableStateOf(false) }
var withdrawAmount by remember { mutableStateOf("") }
var withdrawAddress by remember { mutableStateOf("") }

if (showWithdrawDialog) {
    AlertDialog(
        onDismissRequest = { showWithdrawDialog = false },
        title = { Text("Withdraw REC") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Available: ${wallet.recBalance} REC")
                OutlinedTextField(
                    value = withdrawAmount,
                    onValueChange = { withdrawAmount = it },
                    label = { Text("Amount (REC)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(
                    value = withdrawAddress,
                    onValueChange = { withdrawAddress = it },
                    label = { Text("Wallet Address") },
                    placeholder = { Text("rXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX") }
                )
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        "⚠️ Withdrawals are irreversible. Double-check the address.",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.withdrawREC(
                        withdrawAmount.toFloatOrNull() ?: 0f,
                        withdrawAddress
                    )
                    showWithdrawDialog = false
                },
                enabled = withdrawAmount.toFloatOrNull() != null && withdrawAddress.isNotBlank()
            ) {
                Text("Confirm Withdrawal")
            }
        },
        dismissButton = {
            TextButton(onClick = { showWithdrawDialog = false }) {
                Text("Cancel")
            }
        }
    )
}
```

---

### 4. Blockchain Wallet Screen

**Add Shimmer Loading**:
```kotlin
is Response.Loading -> {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Balance shimmer
        ShimmerEffect(
            type = ShimmerEffect.ShimmerType.CARD,
            modifier = Modifier.fillMaxWidth().height(120.dp)
        )
        // Actions shimmer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            repeat(2) {
                ShimmerEffect(
                    type = ShimmerEffect.ShimmerType.CARD,
                    modifier = Modifier.weight(1f).height(100.dp)
                )
            }
        }
        // Transactions shimmer
        repeat(3) {
            ShimmerEffect(
                type = ShimmerEffect.ShimmerType.CARD,
                modifier = Modifier.fillMaxWidth().height(80.dp)
            )
        }
    }
}
```

**Add Send/Receive Dialogs**:
```kotlin
// Send Dialog
var showSendDialog by remember { mutableStateOf(false) }
var sendAmount by remember { mutableStateOf("") }
var sendAddress by remember { mutableStateOf("") }

if (showSendDialog) {
    AlertDialog(
        onDismissRequest = { showSendDialog = false },
        title = { Text("Send REC") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = sendAddress,
                    onValueChange = { sendAddress = it },
                    label = { Text("Recipient Address") },
                    placeholder = { Text("rXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = sendAmount,
                    onValueChange = { sendAmount = it },
                    label = { Text("Amount (REC)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    "Available: ${wallet.recBalance} REC",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.sendREC(sendAddress, sendAmount.toFloatOrNull() ?: 0f)
                    showSendDialog = false
                },
                enabled = sendAddress.isNotBlank() && sendAmount.toFloatOrNull() != null
            ) {
                Text("Send")
            }
        },
        dismissButton = {
            TextButton(onClick = { showSendDialog = false }) {
                Text("Cancel")
            }
        }
    )
}

// Receive Dialog
var showReceiveDialog by remember { mutableStateOf(false) }

if (showReceiveDialog) {
    AlertDialog(
        onDismissRequest = { showReceiveDialog = false },
        title = { Text("Receive REC") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // QR Code placeholder
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.shapes.medium
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("QR Code", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                
                // Address
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        wallet.address,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace
                    )
                }
                
                Button(
                    onClick = { /* Copy to clipboard */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Copy Address")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { showReceiveDialog = false }) {
                Text("Close")
            }
        }
    )
}
```

---

### 5. Community Screen

**Add Shimmer Loading**:
```kotlin
is Response.Loading -> {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(5) {
            ShimmerEffect(
                type = ShimmerEffect.ShimmerType.CARD,
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )
        }
    }
}
```

**Add Empty State**:
```kotlin
if (posts.isEmpty()) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Forum,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
            Text(
                "No posts yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Be the first to share!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
```

---

### 6. Pickup Screen

**Add Success Animation**:
```kotlin
var showSuccess by remember { mutableStateOf(false) }

if (showSuccess) {
    AlertDialog(
        onDismissRequest = { showSuccess = false },
        icon = {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp)
            )
        },
        title = { Text("Pickup Requested!") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Your pickup request has been submitted successfully.")
                Text(
                    "A collector will be assigned soon.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(onClick = { showSuccess = false; onBack() }) {
                Text("Done")
            }
        }
    )
}
```

---

## 🎨 ViewModel Enhancements

Add these functions to `EngagementViewModel.kt`:

```kotlin
// Sell carbon credits
fun sellCarbonCredits(amount: Float) {
    viewModelScope.launch {
        try {
            val uid = authRepository.currentUser?.uid ?: return@launch
            val result = engagementRepository.sellCarbonCredits(uid, amount)
            if (result is Response.Success) {
                // Refresh wallet
                refresh()
            }
        } catch (e: Exception) {
            // Handle error
        }
    }
}

// Withdraw REC
fun withdrawREC(amount: Float, address: String) {
    viewModelScope.launch {
        try {
            val uid = authRepository.currentUser?.uid ?: return@launch
            val result = engagementRepository.withdrawREC(uid, amount, address)
            if (result is Response.Success) {
                // Refresh wallet
                refresh()
            }
        } catch (e: Exception) {
            // Handle error
        }
    }
}

// Send REC
fun sendREC(toAddress: String, amount: Float) {
    viewModelScope.launch {
        try {
            val uid = authRepository.currentUser?.uid ?: return@launch
            val result = engagementRepository.sendREC(uid, toAddress, amount)
            if (result is Response.Success) {
                // Refresh wallet
                refresh()
            }
        } catch (e: Exception) {
            // Handle error
        }
    }
}
```

---

## 📊 Summary of Changes

### Loading States
- ✅ Replace `CircularProgressIndicator` with `ShimmerEffect`
- ✅ Match shimmer layout to actual content

### Error States
- ✅ Replace plain `Text` with `ErrorState` component
- ✅ Add retry functionality

### Empty States
- ✅ Add friendly empty state messages
- ✅ Use icons and helpful text

### Animations
- ✅ Add `AnimatedVisibility` for content
- ✅ Use `fadeIn()` and `slideInVertically()`

### Functionality
- ✅ Add Sell dialog with amount input
- ✅ Add Withdrawal dialog with address validation
- ✅ Add Send/Receive dialogs for blockchain wallet
- ✅ Add success confirmations

### User Experience
- ✅ Better visual feedback
- ✅ Clearer error messages
- ✅ Smooth transitions
- ✅ Professional appearance

---

## 🔗 Related Components

- **ShimmerEffect**: `.../components/common/ShimmerEffect.kt`
- **ErrorState**: `.../components/common/ErrorState.kt`
- **Material Design 3**: All screens use `MaterialTheme.colorScheme`

---

**Status**: Ready for Implementation  
**Estimated Time**: 4-6 hours  
**Priority**: High (improves user experience significantly)

