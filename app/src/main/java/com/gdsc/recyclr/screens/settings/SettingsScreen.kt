package com.gdsc.recyclr.screens.settings

import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gdsc.recyclr.R
import com.gdsc.recyclr.activities.MainViewModel
import com.gdsc.recyclr.components.composable.BasicTopBar
import com.gdsc.recyclr.components.preferences.LanguageSelectorRow
import com.gdsc.recyclr.components.preferences.ThemeModeSelectorRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenHelpCenter: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenPersonalInformation: () -> Unit,
) {
    val activity = LocalContext.current as ComponentActivity
    val viewModel = hiltViewModel<MainViewModel>(viewModelStoreOwner = activity)
    val context = LocalContext.current
    val isGuest by viewModel.guestModeEnabled.collectAsStateWithLifecycle()

    val versionName = remember(context.packageName) {
        runCatching {
            val pm = context.packageManager
            val pkg = context.packageName
            if (Build.VERSION.SDK_INT >= 33) {
                pm.getPackageInfo(pkg, PackageManager.PackageInfoFlags.of(0)).versionName
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo(pkg, 0).versionName
            }
        }.getOrNull().orEmpty().ifBlank { "—" }
    }

    Scaffold(
        topBar = {
            BasicTopBar(
                title = stringResource(R.string.settings_title),
                onBack = onBack,
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Column(modifier = Modifier.widthIn(max = 600.dp)) {
                    SettingsSectionTitle(stringResource(R.string.settings_section_preferences))
                    SettingsCard {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            ThemeModeSelectorRow()
                            HorizontalDivider(modifier = Modifier.alpha(0.5f))
                            LanguageSelectorRow()
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.widthIn(max = 600.dp)) {
                    SettingsSectionTitle(stringResource(R.string.settings_section_account))
                    SettingsCard {
                        Column {
                            if (isGuest) {
                                SettingsMenuItem(
                                    icon = Icons.AutoMirrored.Outlined.Login,
                                    title = stringResource(R.string.settings_sign_in_create_account),
                                    onClick = { viewModel.setGuestModeEnabled(false) },
                                )
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            }
                            SettingsMenuItem(
                                icon = Icons.Outlined.Person,
                                title = stringResource(R.string.settings_personal_information),
                                onClick = onOpenPersonalInformation,
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            SettingsMenuItem(
                                icon = Icons.Outlined.Notifications,
                                title = stringResource(R.string.settings_notifications_in_app),
                                onClick = {
                                    viewModel.requestOpenHomeAndNotifications()
                                    onBack()
                                },
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            SettingsMenuItem(
                                icon = Icons.Outlined.Security,
                                title = stringResource(R.string.settings_security_privacy),
                                onClick = { },
                            )
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.widthIn(max = 600.dp)) {
                    SettingsSectionTitle(stringResource(R.string.settings_section_app_info))
                    SettingsCard {
                        Column {
                            SettingsMenuItem(
                                icon = Icons.AutoMirrored.Outlined.HelpOutline,
                                title = stringResource(R.string.settings_help_center),
                                onClick = onOpenHelpCenter,
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            SettingsMenuItem(
                                icon = Icons.Outlined.Info,
                                title = stringResource(R.string.settings_about_app),
                                onClick = onOpenAbout,
                            )
                        }
                    }
                }
            }

            if (!isGuest) {
                item {
                    Column(modifier = Modifier.widthIn(max = 600.dp)) {
                        SettingsSectionTitle(stringResource(R.string.settings_section_session))
                        SettingsCard {
                            Column {
                                SettingsMenuItem(
                                    icon = Icons.AutoMirrored.Outlined.Logout,
                                    title = stringResource(R.string.settings_log_out),
                                    onClick = { viewModel.signOut() },
                                )
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                                SettingsMenuItem(
                                    icon = Icons.Outlined.DeleteOutline,
                                    title = stringResource(R.string.settings_delete_account),
                                    onClick = { viewModel.revokeAccess() },
                                    titleColor = MaterialTheme.colorScheme.error,
                                    iconTint = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.widthIn(max = 600.dp)) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = stringResource(R.string.settings_version_line, versionName),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsCard(content: @Composable () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.extraLarge,
        tonalElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        content()
    }
}

@Composable
fun SettingsMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    titleColor: Color = Color.Unspecified,
    iconTint: Color = Color.Unspecified,
    showChevron: Boolean = true,
) {
    val resolvedTitleColor =
        if (titleColor != Color.Unspecified) titleColor else MaterialTheme.colorScheme.onSurface
    val resolvedIconTint =
        if (iconTint != Color.Unspecified) iconTint else MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = resolvedIconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = resolvedTitleColor,
            modifier = Modifier.weight(1f)
        )
        if (showChevron) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}
