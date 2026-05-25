# Ready-to-Use UI Components

Copy-paste these components directly into your Recyclr app.

---

## 1. Enhanced Stat Card with Animation

```kotlin
@Composable
fun AnimatedStatCard(
    icon: ImageVector,
    value: Int,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    var animatedValue by remember { mutableStateOf(0) }
    
    LaunchedEffect(value) {
        animate(
            initialValue = animatedValue.toFloat(),
            targetValue = value.toFloat(),
            animationSpec = tween(1000, easing = FastOutSlowInEasing)
        ) { animValue, _ ->
            animatedValue = animValue.toInt()
        }
    }
    
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = color.copy(alpha = 0.15f)
        ),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = color
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = animatedValue.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Usage
AnimatedStatCard(
    icon = Icons.Default.Stars,
    value = 1250,
    label = "Points",
    color = Color(0xFFFFA726),
    modifier = Modifier.weight(1f)
)
```

---

## 2. Quick Action Button

```kotlin
@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(72.dp),
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// Usage
Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(12.dp)
) {
    QuickActionButton(
        icon = Icons.Default.QrCodeScanner,
        label = "Scan",
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        onClick = { /* Navigate to scan */ },
        modifier = Modifier.weight(1f)
    )
    QuickActionButton(
        icon = Icons.Default.Map,
        label = "Map",
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        onClick = { /* Navigate to map */ },
        modifier = Modifier.weight(1f)
    )
}
```

---

## 3. Enhanced Product Card

```kotlin
@Composable
fun EnhancedProductCard(
    item: ShopItem,
    onItemClick: () -> Unit,
    onWishlistToggle: () -> Unit,
    isWishlisted: Boolean,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    
    ElevatedCard(
        onClick = onItemClick,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (isPressed) 12.dp else 4.dp
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box {
            // Product Image
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                contentScale = ContentScale.Crop
            )
            
            // Wishlist Button
            IconButton(
                onClick = onWishlistToggle,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(Color.White.copy(alpha = 0.9f), CircleShape)
            ) {
                Icon(
                    imageVector = if (isWishlisted) Icons.Filled.Favorite 
                                  else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Wishlist",
                    tint = if (isWishlisted) Color.Red else Color.Gray
                )
            }
        }
        
        // Product Info
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.coins),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFFFFA726)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${item.price} pts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
```

---

## 4. Scanning Frame Animation

```kotlin
@Composable
fun ScanningFrame(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val scanLinePosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    Box(
        modifier = modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        // Corner brackets
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cornerLength = 40.dp.toPx()
            val strokeWidth = 6.dp.toPx()
            val color = Color.White
            
            // Top-left
            drawLine(color, Offset(0f, cornerLength), Offset(0f, 0f), strokeWidth, StrokeCap.Round)
            drawLine(color, Offset(0f, 0f), Offset(cornerLength, 0f), strokeWidth, StrokeCap.Round)
            
            // Top-right
            drawLine(color, Offset(size.width - cornerLength, 0f), Offset(size.width, 0f), strokeWidth, StrokeCap.Round)
            drawLine(color, Offset(size.width, 0f), Offset(size.width, cornerLength), strokeWidth, StrokeCap.Round)
            
            // Bottom-left
            drawLine(color, Offset(0f, size.height - cornerLength), Offset(0f, size.height), strokeWidth, StrokeCap.Round)
            drawLine(color, Offset(0f, size.height), Offset(cornerLength, size.height), strokeWidth, StrokeCap.Round)
            
            // Bottom-right
            drawLine(color, Offset(size.width - cornerLength, size.height), Offset(size.width, size.height), strokeWidth, StrokeCap.Round)
            drawLine(color, Offset(size.width, size.height - cornerLength), Offset(size.width, size.height), strokeWidth, StrokeCap.Round)
        }
        
        // Animated scan line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .offset(y = (280.dp * scanLinePosition))
                .background(Color.Green.copy(alpha = 0.7f))
        )
    }
}
```

---

## 5. Celebration Confetti Animation

```kotlin
@Composable
fun CelebrationConfetti(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    
    val particles = remember {
        List(30) {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                color = listOf(
                    Color(0xFF4CAF50),
                    Color(0xFFFFA726),
                    Color(0xFF42A5F5),
                    Color(0xFFEF5350)
                ).random(),
                size = (4..12).random().dp
            )
        }
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val animatedY by infiniteTransition.animateFloat(
                initialValue = -0.1f,
                targetValue = 1.1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(3000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
            
            drawCircle(
                color = particle.color,
                radius = particle.size.toPx(),
                center = Offset(
                    x = size.width * particle.x,
                    y = size.height * animatedY
                ),
                alpha = 0.8f
            )
        }
    }
}

data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val color: Color,
    val size: Dp
)
```

---

## 6. Profile Achievement Badge

```kotlin
@Composable
fun AchievementBadge(
    badge: UserBadge,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(100.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    if (badge.earned) {
                        Brush.radialGradient(
                            listOf(
                                Color(0xFFFFA726),
                                Color(0xFFFF9800)
                            )
                        )
                    } else {
                        Brush.radialGradient(
                            listOf(
                                Color.Gray.copy(alpha = 0.3f),
                                Color.Gray.copy(alpha = 0.2f)
                            )
                        )
                    }
                )
                .border(
                    3.dp,
                    if (badge.earned) Color(0xFFFFD54F) 
                    else Color.Gray.copy(alpha = 0.3f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = badge.title,
                modifier = Modifier.size(40.dp),
                tint = if (badge.earned) Color.White 
                       else Color.Gray.copy(alpha = 0.5f)
            )
            
            if (!badge.earned) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "Locked",
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = 8.dp, y = 8.dp)
                        .background(Color.White, CircleShape)
                        .padding(4.dp),
                    tint = Color.Gray
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = badge.title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (badge.earned) FontWeight.Bold else FontWeight.Normal,
            color = if (badge.earned) MaterialTheme.colorScheme.onBackground 
                    else Color.Gray,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        
        if (!badge.earned && badge.progress > 0) {
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = badge.progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
```

---

## 7. Swipeable Notification Card

```kotlin
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeableNotificationCard(
    notification: AppNotification,
    onRead: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberDismissState(
        confirmStateChange = {
            when (it) {
                DismissValue.DismissedToEnd -> {
                    onRead()
                    true
                }
                DismissValue.DismissedToStart -> {
                    onDelete()
                    true
                }
                else -> false
            }
        }
    )
    
    SwipeToDismiss(
        state = dismissState,
        modifier = modifier,
        background = {
            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
            val color = when (direction) {
                DismissDirection.StartToEnd -> Color(0xFF4CAF50)
                DismissDirection.EndToStart -> Color(0xFFEF5350)
            }
            val icon = when (direction) {
                DismissDirection.StartToEnd -> Icons.Default.Done
                DismissDirection.EndToStart -> Icons.Default.Delete
            }
            val alignment = when (direction) {
                DismissDirection.StartToEnd -> Alignment.CenterStart
                DismissDirection.EndToStart -> Alignment.CenterEnd
            }
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color, RoundedCornerShape(16.dp))
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                Icon(icon, contentDescription = null, tint = Color.White)
            }
        },
        dismissContent = {
            NotificationCard(notification)
        }
    )
}

@Composable
private fun NotificationCard(notification: AppNotification) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.read) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.28f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(
                        if (notification.read) 
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                        else MaterialTheme.colorScheme.primary
                    )
                    .align(Alignment.Top)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    notification.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (notification.read) FontWeight.Medium 
                                 else FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    notification.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
```

---

## 8. Error State Component

```kotlin
@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Spacer(Modifier.height(16.dp))
        
        Text(
            "Oops! Something went wrong",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
        
        Spacer(Modifier.height(24.dp))
        
        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Try Again")
        }
    }
}
```

---

## 9. Loading Shimmer Effect

```kotlin
@Composable
fun ShimmerEffect(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition()
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.Gray.copy(alpha = alpha))
    )
}

// Usage for product grid
@Composable
fun ProductGridShimmer() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(6) {
            ShimmerEffect(
                modifier = Modifier
                    .aspectRatio(0.75f)
                    .clip(RoundedCornerShape(16.dp))
            )
        }
    }
}
```

---

## 10. Enhanced Bottom Navigation

```kotlin
@Composable
fun EnhancedBottomNavigation(
    selectedRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            BottomNavItem("home", "Home", Icons.Default.Home),
            BottomNavItem("scan", "Scan", Icons.Default.QrCodeScanner),
            BottomNavItem("shop", "Shop", Icons.Default.ShoppingBag),
            BottomNavItem("profile", "Profile", Icons.Default.Person)
        )
        
        items.forEach { item ->
            val isSelected = selectedRoute == item.route
            
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) 
                                    MaterialTheme.colorScheme.primaryContainer
                                else Color.Transparent
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(28.dp),
                            tint = if (isSelected) 
                                MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                label = {
                    Text(
                        item.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold 
                                     else FontWeight.Normal
                    )
                },
                alwaysShowLabel = true
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)
```

---

*Ready-to-Use Components Version: 1.0*
*Last Updated: 2026-05-23*
*All components are production-ready and follow Material Design 3 guidelines*
