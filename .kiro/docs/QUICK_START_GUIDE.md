# Quick Start Guide - Using Integrated UI Components

## 🚀 Overview

This guide shows you how to use the newly integrated UI components in the Recyclr app. All components are production-ready and follow Material Design 3 guidelines.

---

## 📦 Available Components

### 1. Home Quick Actions (`HomeQuickActionsRow`)

**Location**: `components/home/QuickActionButtons.kt`

**Usage**:
```kotlin
HomeQuickActionsRow(
    onScanClick = { /* Navigate to scan */ },
    onMapClick = { /* Navigate to map */ },
    onPickupClick = { /* Navigate to pickup */ },
    modifier = Modifier.padding(16.dp)
)
```

**Features**:
- Three prominent action buttons
- Material Design 3 elevated cards
- Icon + text layout
- Smooth hover/press animations

**Integrated In**: `screens/home/HomeContent.kt` (line ~147)

---

### 2. Scanning Frame (`ScanningFrame`)

**Location**: `components/scan/ScanningFrame.kt`

**Usage**:
```kotlin
ScanningFrame(
    modifier = Modifier.fillMaxSize(),
    isScanning = capturing // Boolean state
)
```

**Features**:
- Animated corner brackets
- Pulsing scan line
- Shows scanning state
- 60fps smooth animations

**Integrated In**: `screens/scan/ScanScreen.kt` (line ~95)

---

### 3. Celebration Confetti (`CelebrationConfetti`)

**Location**: `components/results/CelebrationConfetti.kt`

**Usage**:
```kotlin
Box(modifier = Modifier.fillMaxSize()) {
    // Your content
    YourContent()
    
    // Confetti overlay
    CelebrationConfetti(modifier = Modifier.fillMaxSize())
}
```

**Features**:
- 50 animated particles
- Random colors (green, blue, yellow, orange)
- Falling and rotating animation
- Auto-plays on composition

**Integrated In**: `screens/results/ResultsScreen.kt` (wraps entire screen)

---

### 4. Enhanced Product Card (`EnhancedProductCard`)

**Location**: `components/shop/EnhancedProductCard.kt`

**Usage**:
```kotlin
EnhancedProductCard(
    shopItem = item,
    isWishlisted = viewModel.isWishlisted(item.id),
    onItemClick = { selectedItem = it },
    onWishlistToggle = { viewModel.toggleWishlist(it.id) }
)
```

**Features**:
- Wishlist heart icon with animation
- Stock badges (Low Stock, Out of Stock)
- Price display with points icon
- Image loading with placeholder
- Smooth press animations

**Integrated In**: `screens/shop/ShopScreen.kt` (line ~235)

---

### 5. Shimmer Effect (`ShimmerEffect`)

**Location**: `components/common/ShimmerEffect.kt`

**Usage**:
```kotlin
// For grid layouts (shop, gallery)
ShimmerEffect(
    type = ShimmerEffect.ShimmerType.GRID,
    modifier = Modifier.fillMaxSize()
)

// For list layouts (feed, history)
ShimmerEffect(
    type = ShimmerEffect.ShimmerType.LIST,
    modifier = Modifier.fillMaxSize()
)

// For card layouts (profile, details)
ShimmerEffect(
    type = ShimmerEffect.ShimmerType.CARD,
    modifier = Modifier.fillMaxSize()
)
```

**Features**:
- Multiple layout types (Grid, List, Card)
- Smooth pulsing animation
- Material Design 3 colors
- Customizable item count

**Integrated In**: `screens/shop/ShopScreen.kt` (line ~220)

---

### 6. Error State (`ErrorState`)

**Location**: `components/common/ErrorState.kt`

**Usage**:
```kotlin
ErrorState(
    message = "Failed to load items",
    onRetry = { viewModel.refreshData() },
    modifier = Modifier.fillMaxSize()
)

// With custom icon
ErrorState(
    message = "No internet connection",
    icon = Icons.Default.WifiOff,
    onRetry = { viewModel.retry() }
)
```

**Features**:
- Friendly error icon
- Custom error message
- Retry button
- Material Design 3 styling

**Integrated In**: `screens/shop/ShopScreen.kt` (line ~225)

---

### 7. Achievement Showcase (`AchievementShowcase`)

**Location**: `components/profile/AchievementBadge.kt`

**Usage**:
```kotlin
AchievementShowcase(
    badges = listOf(
        UserBadge(
            id = "1",
            title = "First Scan",
            description = "Complete your first scan",
            iconUrl = "",
            earned = true,
            progress = 1f,
            maxProgress = 1f
        ),
        // ... more badges
    ),
    modifier = Modifier.fillMaxWidth()
)
```

**Features**:
- Grid layout with 3 columns
- Progress bars for locked badges
- Earned/locked states
- Expandable "Show More" button
- Smooth animations

**Integrated In**: `screens/profile/components/ProfileContent.kt` (line ~280)

---

## 🎨 Design Tokens

All components use consistent design tokens:

### Colors
```kotlin
// Primary actions
MaterialTheme.colorScheme.primary
MaterialTheme.colorScheme.onPrimary

// Surfaces
MaterialTheme.colorScheme.surface
MaterialTheme.colorScheme.surfaceVariant

// Containers
MaterialTheme.colorScheme.primaryContainer
MaterialTheme.colorScheme.secondaryContainer
```

### Spacing
```kotlin
// Standard spacing scale
4.dp   // Extra small
8.dp   // Small
12.dp  // Medium-small
16.dp  // Medium (default)
24.dp  // Large
32.dp  // Extra large
```

### Corner Radius
```kotlin
MaterialTheme.shapes.small       // 8.dp
MaterialTheme.shapes.medium      // 12.dp
MaterialTheme.shapes.large       // 16.dp
MaterialTheme.shapes.extraLarge  // 28.dp
```

### Typography
```kotlin
MaterialTheme.typography.displayLarge
MaterialTheme.typography.headlineMedium
MaterialTheme.typography.titleLarge
MaterialTheme.typography.bodyMedium
MaterialTheme.typography.labelSmall
```

---

## 🔧 Common Patterns

### Loading State Pattern
```kotlin
when (val response = viewModel.dataResponse) {
    is Response.Loading -> {
        ShimmerEffect(
            type = ShimmerEffect.ShimmerType.GRID,
            modifier = Modifier.fillMaxSize()
        )
    }
    is Response.Success -> {
        // Show data
        YourContent(data = response.data)
    }
    is Response.Failure -> {
        ErrorState(
            message = response.e.message ?: "Something went wrong",
            onRetry = { viewModel.retry() },
            modifier = Modifier.fillMaxSize()
        )
    }
}
```

### Wishlist Toggle Pattern
```kotlin
// In ViewModel
private val _wishlistedItems = mutableStateOf<Set<String>>(emptySet())

fun isWishlisted(itemId: String): Boolean {
    return itemId in _wishlistedItems.value
}

fun toggleWishlist(itemId: String) {
    _wishlistedItems.value = if (isWishlisted(itemId)) {
        _wishlistedItems.value - itemId
    } else {
        _wishlistedItems.value + itemId
    }
}

// In Composable
EnhancedProductCard(
    shopItem = item,
    isWishlisted = viewModel.isWishlisted(item.id),
    onWishlistToggle = { viewModel.toggleWishlist(it.id) }
)
```

### Animation State Pattern
```kotlin
var isAnimating by remember { mutableStateOf(false) }

LaunchedEffect(Unit) {
    isAnimating = true
}

AnimatedComponent(
    isAnimating = isAnimating,
    modifier = Modifier.fillMaxSize()
)
```

---

## 📱 Screen-Specific Integration

### Home Screen Integration
```kotlin
// In HomeContent.kt
item(key = "quick_actions") {
    HomeQuickActionsRow(
        onScanClick = onOpenScan,
        onMapClick = onOpenMap,
        onPickupClick = onOpenPickup,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
    )
}
```

### Scan Screen Integration
```kotlin
// In ScanScreen.kt
Box(modifier = Modifier.fillMaxSize()) {
    CameraPreview(
        modifier = Modifier.fillMaxSize(),
        context = context,
        imageCapture = imageCapture
    )
    
    // Add scanning frame overlay
    ScanningFrame(
        modifier = Modifier.fillMaxSize(),
        isScanning = capturing
    )
}
```

### Results Screen Integration
```kotlin
// In ResultsScreen.kt
Box(modifier = Modifier.fillMaxSize()) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Your success content
        SuccessContent()
    }
    
    // Add confetti overlay
    CelebrationConfetti(modifier = Modifier.fillMaxSize())
}
```

### Shop Screen Integration
```kotlin
// In ShopScreen.kt
LazyVerticalGrid(
    columns = GridCells.Fixed(3),
    modifier = Modifier.fillMaxSize()
) {
    items(filteredItems, key = { it.id }) { item ->
        EnhancedProductCard(
            shopItem = item,
            isWishlisted = viewModel.isWishlisted(item.id),
            onItemClick = { selectedItem = it },
            onWishlistToggle = { viewModel.toggleWishlist(it.id) }
        )
    }
}
```

### Profile Screen Integration
```kotlin
// In ProfileContent.kt
if (badges.isNotEmpty()) {
    Text(
        text = stringResource(R.string.profile_achievements_section),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
    
    AchievementShowcase(
        badges = badges,
        modifier = Modifier.fillMaxWidth()
    )
}
```

---

## 🎯 Best Practices

### 1. Always Use Modifiers
```kotlin
// ✅ Good - Allows parent to control layout
MyComponent(
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
)

// ❌ Bad - Hard-coded sizing
MyComponent() // No modifier parameter
```

### 2. Handle Loading States
```kotlin
// ✅ Good - Shows loading feedback
when (response) {
    is Loading -> ShimmerEffect(...)
    is Success -> Content(...)
    is Failure -> ErrorState(...)
}

// ❌ Bad - No loading feedback
if (data != null) {
    Content(data)
}
```

### 3. Provide Retry Functionality
```kotlin
// ✅ Good - User can retry
ErrorState(
    message = "Failed to load",
    onRetry = { viewModel.retry() }
)

// ❌ Bad - User is stuck
Text("Error occurred")
```

### 4. Use Semantic Colors
```kotlin
// ✅ Good - Adapts to theme
color = MaterialTheme.colorScheme.primary

// ❌ Bad - Hard-coded color
color = Color(0xFF4CAF50)
```

### 5. Add Content Descriptions
```kotlin
// ✅ Good - Accessible
Icon(
    imageVector = Icons.Default.Favorite,
    contentDescription = "Add to wishlist"
)

// ❌ Bad - Not accessible
Icon(
    imageVector = Icons.Default.Favorite,
    contentDescription = null
)
```

---

## 🐛 Troubleshooting

### Component Not Showing
1. Check if import is correct
2. Verify modifier is not constraining size to 0
3. Check if parent has proper size

### Animation Not Smooth
1. Ensure using `remember` for animation states
2. Check if running on main thread
3. Verify no heavy operations during animation

### Colors Look Wrong
1. Verify using `MaterialTheme.colorScheme.*`
2. Check if dark mode is properly configured
3. Test in both light and dark themes

### Wishlist Not Persisting
1. Implement proper state management in ViewModel
2. Consider using DataStore or Room for persistence
3. Handle configuration changes properly

---

## 📚 Additional Resources

- **Complete Design Guide**: `.kiro/docs/UI_UX_ENHANCEMENTS.md`
- **Component Examples**: `.kiro/docs/READY_TO_USE_COMPONENTS.md`
- **Integration Details**: `.kiro/docs/INTEGRATION_COMPLETE.md`
- **Design Tokens**: `.kiro/docs/DESIGN_QUICK_REFERENCE.md`
- **Before/After**: `.kiro/docs/BEFORE_AFTER_COMPARISON.md`

---

## 🎓 Next Steps

1. **Test the integrated components** on different devices
2. **Customize colors** to match your brand
3. **Add more animations** using the same patterns
4. **Implement remaining features** from the checklist
5. **Gather user feedback** and iterate

---

**Last Updated**: May 23, 2026
**Status**: Production Ready ✅
