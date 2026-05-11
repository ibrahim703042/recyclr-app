package com.gdsc.recyclr.screens.profile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.design.CurvedHeaderShape
import com.gdsc.recyclr.components.preferences.LanguageSelectorRow
import com.gdsc.recyclr.components.preferences.ThemeModeSelectorRow
import com.gdsc.recyclr.components.preferences.ThemeToggleIconButton
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.User
import com.gdsc.recyclr.domain.model.UserImpact
import com.gdsc.recyclr.domain.model.engagement.UserBadge
import com.gdsc.recyclr.ui.theme.ForestGreen
import com.gdsc.recyclr.ui.theme.LeafGreen
import com.gdsc.recyclr.ui.theme.RecyclrThemeColors
import com.gdsc.recyclr.ui.theme.recyclrMutedText
import com.gdsc.recyclr.ui.theme.recyclrScreenBackground

@Composable
fun ProfileContent(
    padding: PaddingValues,
    user: User?,
    impactResponse: Response<UserImpact>,
    badges: List<UserBadge>,
    onOpenWallet: () -> Unit,
) {
    val displayName = user?.displayName?.takeIf { it.isNotBlank() }
        ?: stringResource(R.string.profile_sample_name)
    val email = user?.email?.takeIf { it.isNotBlank() }
        ?: stringResource(R.string.profile_sample_email)
    val points = when (impactResponse) {
        is Response.Success -> impactResponse.data?.pointsBalance ?: 120
        else -> 120
    }
    val trees = when (impactResponse) {
        is Response.Success -> impactResponse.data?.treesEquivalent ?: 1
        else -> 1
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(recyclrScreenBackground())
            .padding(padding),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            CurvedHeaderShape(color = RecyclrThemeColors.headerBackground, height = 170.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.profile_title),
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
                ThemeToggleIconButton()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(88.dp))
            if (!user?.photoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = user!!.photoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(108.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colors.surface, CircleShape)
                        .padding(4.dp),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(108.dp)
                        .clip(CircleShape)
                        .background(ForestGreen)
                        .padding(4.dp)
                        .background(MaterialTheme.colors.surface, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(88.dp),
                        tint = Color(0xFFB0B0B0),
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-18).dp),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 0.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = displayName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "$points", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Image(
                            painter = painterResource(R.drawable.coins),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(start = 6.dp)
                                .size(16.dp),
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 0.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(R.drawable.people_planting_a_tree),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.profile_trees_saved, trees),
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            if (badges.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = MaterialTheme.colors.surface,
                    elevation = 0.dp,
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = stringResource(R.string.profile_badges), fontWeight = FontWeight.Bold)
                        badges.forEach { badge ->
                            Text(
                                text = if (badge.earned) "✓ ${badge.title}" else "○ ${badge.title}",
                                color = if (badge.earned) LeafGreen else recyclrMutedText(),
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenWallet),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 0.dp,
            ) {
                Text(
                    text = stringResource(R.string.profile_open_wallet),
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 0.dp,
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ThemeModeSelectorRow()
                    LanguageSelectorRow()
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            ProfileMenuItem(title = stringResource(R.string.profile_menu_address))
            ProfileMenuItem(title = stringResource(R.string.profile_menu_personal_info))
            ProfileMenuItem(title = stringResource(R.string.profile_menu_exchange_points))
            ProfileMenuItem(title = stringResource(R.string.profile_menu_notifications))

            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 0.dp,
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = stringResource(R.string.profile_email, email), fontSize = 14.sp)
                }
            }

            if (impactResponse is Response.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = MaterialTheme.colors.primary,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileMenuItem(title: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = title, fontSize = 15.sp)
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFFB0B0B0),
            )
        }
    }
}
