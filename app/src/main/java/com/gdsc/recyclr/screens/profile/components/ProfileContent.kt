package com.gdsc.recyclr.screens.profile.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.design.recyclrContentWidth
import com.gdsc.recyclr.components.profile.AchievementShowcase
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.User
import com.gdsc.recyclr.domain.model.UserImpact
import com.gdsc.recyclr.domain.model.engagement.UserBadge
import com.gdsc.recyclr.domain.model.UserRole
import com.gdsc.recyclr.components.settings.SettingsGradients
import com.gdsc.recyclr.components.settings.SettingsCard
import com.gdsc.recyclr.components.settings.SettingsMenuItem
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    padding: PaddingValues,
    user: User?,
    impactResponse: Response<UserImpact>,
    badges: List<UserBadge>,
    isGuest: Boolean,
    onGuestSignIn: () -> Unit,
    onLogoutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    onOpenWallet: () -> Unit,
    onOpenBlockchainWallet: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAdminDashboard: () -> Unit,
    onOpenCollectorDashboard: () -> Unit,
) {
    val displayName = when {
        isGuest -> stringResource(R.string.profile_guest_name)
        else -> user?.displayName?.takeIf { it.isNotBlank() } ?: "Green Hero"
    }
    val email = when {
        isGuest -> stringResource(R.string.profile_guest_email_hint)
        else -> user?.email?.takeIf { it.isNotBlank() } ?: "nature.lover@example.com"
    }
    
    val impact = (impactResponse as? Response.Success)?.data
    val points = impact?.pointsBalance ?: 0
    val trees = impact?.treesEquivalent ?: 0
    val co2Saved = impact?.co2SavedKg ?: 0f

    val scrollState = rememberScrollState()
    
    // Threshold for when the profile header "collapses" into the TopBar
    val collapseThreshold = 180.dp
    val showCollapsedInfo by remember {
        derivedStateOf { scrollState.value > 400 } // Rough estimate in pixels
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(padding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp)) // Space for TopBar

            Column(
                modifier = Modifier
                    .recyclrContentWidth()
                    .fillMaxWidth()
            ) {
                // User Header Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .alpha(if (showCollapsedInfo) 0f else 1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!user?.photoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = user!!.photoUrl,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (isGuest) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                            onClick = onGuestSignIn,
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = stringResource(R.string.profile_guest_sign_in_banner),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(onClick = onGuestSignIn) {
                                    Text(stringResource(R.string.settings_sign_in_create_account))
                                }
                            }
                        }
                    }
                }

                // Impact Statistics Grid
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ImpactStatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Eco,
                        value = String.format(Locale.getDefault(), "%.1fkg", co2Saved),
                        label = "CO2 Saved",
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                    ImpactStatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Stars,
                        value = points.toString(),
                        label = "Points",
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                    ImpactStatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Forest,
                        value = trees.toString(),
                        label = "Trees",
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Actions Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.profile_my_activity_section),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    ProfileActionItem(
                        icon = Icons.Outlined.AccountBalanceWallet,
                        title = stringResource(R.string.profile_my_wallet_title),
                        subtitle = stringResource(R.string.profile_my_wallet_subtitle),
                        onClick = onOpenWallet
                    )
                    
                    ProfileActionItem(
                        icon = Icons.Outlined.Link,
                        title = stringResource(R.string.wallet_blockchain_entry),
                        subtitle = stringResource(R.string.wallet_blockchain_subtitle),
                        onClick = onOpenBlockchainWallet
                    )
                    
                    ProfileActionItem(
                        icon = Icons.Outlined.History,
                        title = stringResource(R.string.profile_recycling_history_title),
                        subtitle = stringResource(R.string.profile_recycling_history_subtitle),
                        onClick = { /* TODO */ }
                    )

                    if (user?.role == UserRole.ADMIN) {
                        ProfileActionItem(
                            icon = Icons.Outlined.AdminPanelSettings,
                            title = "Admin Dashboard",
                            subtitle = "Manage users and app data",
                            onClick = onOpenAdminDashboard
                        )
                    }

                    if (user?.role == UserRole.COLLECTOR || user?.role == UserRole.ADMIN) {
                        ProfileActionItem(
                            icon = Icons.Outlined.LocalShipping,
                            title = "Collector Dashboard",
                            subtitle = "View and manage assigned pickups",
                            onClick = onOpenCollectorDashboard
                        )
                    }

                    if (!isGuest) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = stringResource(R.string.profile_section_session),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        SettingsCard {
                            Column {
                                SettingsMenuItem(
                                    icon = Icons.AutoMirrored.Outlined.Logout,
                                    title = stringResource(R.string.settings_log_out),
                                    onClick = onLogoutClick,
                                    iconGradient = SettingsGradients.Logout,
                                )
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                                SettingsMenuItem(
                                    icon = Icons.Outlined.DeleteOutline,
                                    title = stringResource(R.string.settings_delete_account),
                                    onClick = onDeleteAccountClick,
                                    titleColor = MaterialTheme.colorScheme.error,
                                    iconGradient = SettingsGradients.Delete,
                                )
                            }
                        }
                    }

                    if (badges.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = stringResource(R.string.profile_achievements_section),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        AchievementShowcase(
                            badges = badges,
                            onBadgeClick = { /* TODO: Show badge details */ },
                            onViewAll = { /* TODO: Navigate to achievements screen */ },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // Animated TopBar with Collapsed User Info
        CenterAlignedTopAppBar(
            modifier = Modifier.fillMaxWidth(),
            title = {
                AnimatedVisibility(
                    visible = showCollapsedInfo,
                    enter = fadeIn() + slideInVertically { it / 2 },
                    exit = fadeOut() + slideOutVertically { it / 2 }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (!user?.photoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = user!!.photoUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.padding(4.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                if (!showCollapsedInfo) {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            actions = {
                IconButton(onClick = onOpenSettings) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings"
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = if (showCollapsedInfo) MaterialTheme.colorScheme.surface else Color.Transparent
            )
        )
    }
}

@Composable
fun ImpactStatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String,
    containerColor: Color
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = containerColor.copy(alpha = 0.7f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
            Text(text = label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun ProfileActionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun BadgeIcon(badge: UserBadge) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    if (badge.earned) MaterialTheme.colorScheme.primary 
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = if (badge.earned) Color.White else MaterialTheme.colorScheme.outline
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = badge.title.split(" ").first(),
            style = MaterialTheme.typography.labelSmall,
            color = if (badge.earned) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.outline
        )
    }
}
