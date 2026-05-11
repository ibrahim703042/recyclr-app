package com.gdsc.recyclr.screens.engagement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.preferences.ThemeToggleIconButton
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.engagement.CommunityPost
import com.gdsc.recyclr.domain.model.engagement.DonationCause
import com.gdsc.recyclr.domain.model.engagement.LeaderboardEntry
import com.gdsc.recyclr.domain.model.engagement.PickupRequestDraft
import com.gdsc.recyclr.domain.model.engagement.RecWallet
import com.gdsc.recyclr.domain.model.engagement.WeeklyChallenge
import com.gdsc.recyclr.ui.theme.LeafGreen
import com.gdsc.recyclr.ui.theme.recyclrScreenBackground
import kotlinx.coroutines.launch

@Composable
fun ChallengeScreen(onBack: () -> Unit, viewModel: EngagementViewModel = hiltViewModel()) {
    FeatureScaffold(title = stringResource(R.string.challenge_title), onBack = onBack) {
        when (val response = viewModel.challengeResponse) {
            is Response.Loading -> Text(stringResource(R.string.loading))
            is Response.Failure -> Text(response.e.message ?: stringResource(R.string.error_generic))
            is Response.Success -> response.data?.let { ChallengeBody(it) }
        }
    }
}

@Composable
private fun ChallengeBody(challenge: WeeklyChallenge) {
    val progress = challenge.currentScans.toFloat() / challenge.targetScans.coerceAtLeast(1)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(challenge.title, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Text(challenge.description)
        LinearProgressIndicator(
            progress = progress.coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(8.dp)),
            color = LeafGreen,
        )
        Text(stringResource(R.string.challenge_reward, challenge.rewardPoints))
        Text(stringResource(R.string.challenge_ends_in, challenge.endsInDays))
    }
}

@Composable
fun LeaderboardScreen(onBack: () -> Unit, viewModel: EngagementViewModel = hiltViewModel()) {
    FeatureScaffold(title = stringResource(R.string.leaderboard_title), onBack = onBack) {
        when (val response = viewModel.leaderboardResponse) {
            is Response.Loading -> Text(stringResource(R.string.loading))
            is Response.Failure -> Text(response.e.message ?: stringResource(R.string.error_generic))
            is Response.Success -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    response.data.orEmpty().forEach { LeaderboardRow(it) }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRow(entry: LeaderboardEntry) {
    Card(shape = RoundedCornerShape(16.dp), elevation = 0.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("#${entry.rank} ${entry.name}", fontWeight = if (entry.isCurrentUser) FontWeight.Bold else FontWeight.Normal)
            Text("${entry.points}")
        }
    }
}

@Composable
fun CommunityScreen(onBack: () -> Unit, viewModel: EngagementViewModel = hiltViewModel()) {
    FeatureScaffold(title = stringResource(R.string.community_title), onBack = onBack) {
        when (val response = viewModel.communityResponse) {
            is Response.Loading -> Text(stringResource(R.string.loading))
            is Response.Failure -> Text(response.e.message ?: stringResource(R.string.error_generic))
            is Response.Success -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(response.data.orEmpty()) { post -> CommunityCard(post) }
                }
            }
        }
    }
}

@Composable
private fun CommunityCard(post: CommunityPost) {
    Card(shape = RoundedCornerShape(18.dp), elevation = 0.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(post.groupName, color = LeafGreen, fontSize = 12.sp)
            Text(post.author, fontWeight = FontWeight.Bold)
            Text(post.message)
            Spacer(modifier = Modifier.height(6.dp))
            Text(stringResource(R.string.home_post_likes, post.likes), fontSize = 12.sp)
        }
    }
}

@Composable
fun WalletScreen(onBack: () -> Unit, viewModel: EngagementViewModel = hiltViewModel()) {
    FeatureScaffold(title = stringResource(R.string.wallet_title), onBack = onBack) {
        when (val response = viewModel.walletResponse) {
            is Response.Loading -> Text(stringResource(R.string.loading))
            is Response.Failure -> Text(response.e.message ?: stringResource(R.string.error_generic))
            is Response.Success -> response.data?.let { WalletBody(it) }
        }
    }
}

@Composable
private fun WalletBody(wallet: RecWallet) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(stringResource(R.string.wallet_rec_balance, wallet.recBalance), fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(stringResource(R.string.wallet_points_balance, wallet.pointsBalance))
        Text(stringResource(R.string.wallet_conversion_rate, wallet.conversionRate))
        Text(stringResource(R.string.wallet_carbon_credits, wallet.carbonCreditsTonnes))
        Text(stringResource(R.string.wallet_lifetime_rec, wallet.lifetimeRecMinted))
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(backgroundColor = LeafGreen),
        ) { Text(stringResource(R.string.wallet_donate_rec), color = Color.White) }
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
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text(stringResource(R.string.pickup_address)) }, modifier = Modifier.fillMaxWidth())
            PickupCheck(stringResource(R.string.scan_manual_plastic), plastic) { plastic = it }
            PickupCheck(stringResource(R.string.scan_manual_paper), paper) { paper = it }
            PickupCheck(stringResource(R.string.category_glass), glass) { glass = it }
            Text(stringResource(R.string.pickup_weight, weight))
            Slider(value = weight, onValueChange = { weight = it }, valueRange = 1f..50f)
            PickupCheck(stringResource(R.string.pickup_repeat), repeatWeeks) { repeatWeeks = it }
            Button(
                onClick = {
                    scope.launch {
                        val types = buildList {
                            if (plastic) add("Plastic")
                            if (paper) add("Paper")
                            if (glass) add("Glass")
                        }
                        submitted = viewModel.submitPickup(
                            PickupRequestDraft(
                                address = address,
                                itemTypes = types,
                                estimatedKg = weight,
                                repeatEveryWeeks = if (repeatWeeks) 2 else null,
                            ),
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = LeafGreen),
                modifier = Modifier.fillMaxWidth(),
            ) { Text(stringResource(R.string.pickup_submit), color = Color.White) }
            if (submitted) Text(stringResource(R.string.pickup_success))
        }
    }
}

@Composable
private fun PickupCheck(label: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = onChecked)
        Text(label)
    }
}

@Composable
fun DonationHubSection(viewModel: EngagementViewModel = hiltViewModel()) {
    when (val response = viewModel.donationsResponse) {
        is Response.Loading -> Text(stringResource(R.string.loading))
        is Response.Failure -> Text(response.e.message ?: stringResource(R.string.error_generic))
        is Response.Success -> {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                response.data.orEmpty().forEach { cause -> DonationCard(cause) }
            }
        }
    }
}

@Composable
private fun DonationCard(cause: DonationCause) {
    Card(shape = RoundedCornerShape(18.dp), elevation = 0.dp) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(cause.title, fontWeight = FontWeight.Bold)
            Text(cause.description, fontSize = 13.sp)
            Text(stringResource(R.string.donation_cost, cause.pointsCost))
            Button(onClick = {}, colors = ButtonDefaults.buttonColors(backgroundColor = LeafGreen)) {
                Text(stringResource(R.string.donation_redeem), color = Color.White)
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
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = { ThemeToggleIconButton() },
                backgroundColor = Color.Transparent,
                elevation = 0.dp,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(recyclrScreenBackground())
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            content()
        }
    }
}
