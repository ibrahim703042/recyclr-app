package com.gdsc.recyclr.screens.engagement

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.composable.BasicTopBar
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.UserRole
import com.gdsc.recyclr.domain.model.engagement.*
import com.gdsc.recyclr.ui.theme.PrimaryGreen
import kotlinx.coroutines.launch

@Composable
fun ChallengeScreen(onBack: () -> Unit, viewModel: EngagementViewModel = hiltViewModel()) {
    FeatureScaffold(title = stringResource(R.string.challenge_title), onBack = onBack) {
        when (val response = viewModel.challengeResponse) {
            is Response.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            is Response.Failure -> Text(response.e.message ?: stringResource(R.string.error_generic))
            is Response.Success -> response.data?.let { ChallengeBody(it) }
        }
    }
}

@Composable
private fun ChallengeBody(challenge: WeeklyChallenge) {
    val progress = challenge.currentScans.toFloat() / challenge.targetScans.coerceAtLeast(1)
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(challenge.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(challenge.description, style = MaterialTheme.typography.bodyLarge)
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${challenge.currentScans} / ${challenge.targetScans} scans", style = MaterialTheme.typography.labelMedium)
                    Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
            }
            
            ListItem(
                headlineContent = { Text(stringResource(R.string.challenge_reward, challenge.rewardPoints)) },
                supportingContent = { Text(stringResource(R.string.challenge_ends_in, challenge.endsInDays)) },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )
        }
    }
}

@Composable
fun LeaderboardScreen(onBack: () -> Unit, viewModel: EngagementViewModel = hiltViewModel()) {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    Scaffold(
        topBar = {
            BasicTopBar(title = stringResource(R.string.leaderboard_title), onBack = onBack)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("Weekly", modifier = Modifier.padding(16.dp))
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("All Time", modifier = Modifier.padding(16.dp))
                }
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                    Text("Friends", modifier = Modifier.padding(16.dp))
                }
            }
            
            Box(modifier = Modifier.weight(1f)) {
                when (val response = viewModel.leaderboardResponse) {
                    is Response.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                    is Response.Failure -> Text(response.e.message ?: stringResource(R.string.error_generic))
                    is Response.Success -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            viewModel.challengeResponse.let { 
                                if (it is Response.Success && it.data != null) {
                                    ChallengeBody(it.data)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                            
                            response.data.orEmpty().forEach { LeaderboardRow(it) }
                        }
                    }
                }
            }
            
            viewModel.currentUserLeaderboardEntry?.let { entry ->
                if (selectedTab == 0) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        tonalElevation = 8.dp,
                        shadowElevation = 8.dp,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Your Rank", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            LeaderboardRow(entry.copy(isCurrentUser = true))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRow(entry: LeaderboardEntry) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = if (entry.isCurrentUser) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
        border = if (entry.isCurrentUser) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = entry.rank.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(28.dp)
                )
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(entry.name.take(1).uppercase())
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = entry.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (entry.isCurrentUser) FontWeight.Bold else FontWeight.Normal
                    )
                    if (entry.role != UserRole.USER) {
                        Surface(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = MaterialTheme.shapes.extraSmall
                        ) {
                            Text(
                                text = entry.role.name,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 4.dp),
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            }
            Text(
                text = "${entry.points} pts",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun CommunityScreen(onBack: () -> Unit, viewModel: EngagementViewModel = hiltViewModel()) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showCreatePost by remember { mutableStateOf(false) }
    val userRole = viewModel.userRole

    Scaffold(
        topBar = {
            BasicTopBar(title = stringResource(R.string.community_title), onBack = onBack)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreatePost = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create Post")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("For You", modifier = Modifier.padding(16.dp))
                }
                if (userRole != UserRole.USER) {
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                        Text("Reports", modifier = Modifier.padding(16.dp))
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (val response = viewModel.communityResponse) {
                    is Response.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                    is Response.Failure -> Text(response.e.message ?: stringResource(R.string.error_generic))
                    is Response.Success -> {
                        val posts = response.data.orEmpty().filter { 
                            if (selectedTab == 0) !it.isReport else it.isReport 
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            posts.forEach { post -> CommunityCard(post) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CommunityCard(post: CommunityPost) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(post.author.take(1).uppercase())
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(post.author, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(post.groupName, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(post.message, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like"
                        )
                    }
                    Text("${post.likes}", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Comment"
                        )
                    }
                    Text("8", style = MaterialTheme.typography.labelMedium)
                }
                IconButton(onClick = { /* TODO */ }) {
                    Icon(imageVector = Icons.Outlined.Share, contentDescription = "Share")
                }
            }
        }
    }
}

@Composable
fun WalletScreen(
    onBack: () -> Unit,
    onOpenBlockchainWallet: () -> Unit = {},
    viewModel: EngagementViewModel = hiltViewModel(),
) {
    FeatureScaffold(title = stringResource(R.string.wallet_title), onBack = onBack) {
        when (val response = viewModel.walletResponse) {
            is Response.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            is Response.Failure -> Text(response.e.message ?: stringResource(R.string.error_generic))
            is Response.Success -> response.data?.let { WalletBody(it, onOpenBlockchainWallet) }
        }
    }
}

@Composable
private fun WalletBody(wallet: RecWallet, onOpenBlockchainWallet: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Total Balance", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f), style = MaterialTheme.typography.labelMedium)
                Text(
                    stringResource(R.string.wallet_rec_balance, wallet.recBalance),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    stringResource(R.string.wallet_points_balance, wallet.pointsBalance),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Activity Graph", color = Color.White.copy(alpha = 0.5f), style = MaterialTheme.typography.labelSmall)
                }
            }
        }
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)),
            shape = MaterialTheme.shapes.large
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Carbon Credits Earned", style = MaterialTheme.typography.labelMedium)
                    Text("🌱 ${wallet.carbonCreditsTonnes} tCO2", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Market value: ~$${String.format("%.2f", wallet.carbonCreditsTonnes * 10)}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
                Button(onClick = { /* TODO */ }, shape = MaterialTheme.shapes.medium) {
                    Text("Sell")
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {},
                modifier = Modifier.weight(1f).height(56.dp),
                shape = MaterialTheme.shapes.large
            ) { 
                Text(stringResource(R.string.wallet_donate_rec), fontWeight = FontWeight.Bold) 
            }
            OutlinedButton(
                onClick = {},
                modifier = Modifier.weight(1f).height(56.dp),
                shape = MaterialTheme.shapes.large
            ) { 
                Text("Withdraw", fontWeight = FontWeight.Bold) 
            }
        }

        OutlinedButton(
            onClick = onOpenBlockchainWallet,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = MaterialTheme.shapes.large,
        ) {
            Text(stringResource(R.string.wallet_blockchain_entry), fontWeight = FontWeight.Bold)
        }
        
        Text("Transaction History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            TransactionItem("Scan Reward", "+5 REC", "2 hours ago")
            TransactionItem("Tree Redemption", "-2 REC", "Yesterday")
            TransactionItem("Carbon Sale", "+1.5 REC", "3 days ago")
        }
    }
}

@Composable
private fun TransactionItem(title: String, amount: String, time: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text(text = time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(
            text = amount, 
            style = MaterialTheme.typography.titleMedium, 
            fontWeight = FontWeight.Black,
            color = if (amount.startsWith("+")) PrimaryGreen else MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun PickupScreen(onBack: () -> Unit, viewModel: EngagementViewModel = hiltViewModel()) {
    val scope = rememberCoroutineScope()
    var address by remember { mutableStateOf("") }
    var plastic by remember { mutableStateOf(true) }
    var paper by remember { mutableStateOf(false) }
    var glass by remember { mutableStateOf(false) }
    var weight by remember { mutableStateOf(5f) }
    var repeatWeeks by remember { mutableStateOf(false) }
    var submitted by remember { mutableStateOf(false) }

    FeatureScaffold(title = stringResource(R.string.pickup_title), onBack = onBack) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text(stringResource(R.string.pickup_address)) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )
            
            Text("What are we picking up?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilterChip(selected = plastic, onClick = { plastic = !plastic }, label = { Text("Plastic") })
                FilterChip(selected = paper, onClick = { paper = !paper }, label = { Text("Paper") })
                FilterChip(selected = glass, onClick = { glass = !glass }, label = { Text("Glass") })
            }

            Column {
                Text(stringResource(R.string.pickup_weight, weight.toInt()), style = MaterialTheme.typography.bodyMedium)
                Slider(value = weight, onValueChange = { weight = it }, valueRange = 1f..50f)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = repeatWeeks, onCheckedChange = { repeatWeeks = it })
                Spacer(modifier = Modifier.width(12.dp))
                Text("Repeat every 2 weeks")
            }

            Button(
                onClick = {
                    scope.launch {
                        val types = buildList {
                            if (plastic) add("Plastic")
                            if (paper) add("Paper")
                            if (glass) add("Glass")
                        }
                        submitted = try {
                            viewModel.submitPickup(
                                PickupRequestDraft(
                                    address = address,
                                    itemTypes = types,
                                    estimatedKg = weight,
                                    repeatEveryWeeks = if (repeatWeeks) 2 else null,
                                ),
                            )
                        } catch (_: Exception) {
                            false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.large
            ) { 
                Text(stringResource(R.string.pickup_submit), fontWeight = FontWeight.Bold) 
            }
            
            if (submitted) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        stringResource(R.string.pickup_success),
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun DonationHubSection(viewModel: EngagementViewModel = hiltViewModel()) {
    when (val response = viewModel.donationsResponse) {
        is Response.Loading -> Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        is Response.Failure -> Text(response.e.message ?: stringResource(R.string.error_generic))
        is Response.Success -> {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                response.data.orEmpty().forEach { cause -> DonationCard(cause) }
            }
        }
    }
}

@Composable
private fun DonationCard(cause: DonationCause) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.VolunteerActivism,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
                
                Surface(
                    modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp),
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = cause.pointsCost.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Black
                        )
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(R.drawable.coins),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Unspecified
                        )
                    }
                }
            }
            
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = cause.title, 
                    style = MaterialTheme.typography.titleLarge, 
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = cause.description, 
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = { /* TODO */ },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(
                        text = "Support this cause",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureScaffold(
    title: String,
    onBack: () -> Unit,
    content: @Composable () -> Unit,
) {
    Scaffold(
        topBar = {
            BasicTopBar(title = title, onBack = onBack)
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            content()
        }
    }
}
