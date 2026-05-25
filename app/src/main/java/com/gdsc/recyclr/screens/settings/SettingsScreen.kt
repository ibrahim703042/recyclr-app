package com.gdsc.recyclr.screens.settings

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gdsc.recyclr.R
import com.gdsc.recyclr.activities.MainViewModel
import com.gdsc.recyclr.components.design.RecyclrDetailScaffold
import com.gdsc.recyclr.components.design.RecyclrLayout
import com.gdsc.recyclr.components.design.recyclrContentWidth
import com.gdsc.recyclr.components.preferences.LanguageSelectorRow
import com.gdsc.recyclr.components.preferences.ThemeModeSelectorRow
import com.gdsc.recyclr.components.settings.DestructiveActionDialog
import com.gdsc.recyclr.components.settings.SettingsCard
import com.gdsc.recyclr.components.settings.SettingsGradients
import com.gdsc.recyclr.components.settings.SettingsMenuItem
import com.gdsc.recyclr.components.settings.SettingsSectionTitle
import com.gdsc.recyclr.util.readAppVersionInfo

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
    val settingsVm = hiltViewModel<SettingsViewModel>()
    val context = LocalContext.current
    val isGuest by viewModel.guestModeEnabled.collectAsStateWithLifecycle()
    val unreadCount by settingsVm.unreadNotificationCount.collectAsStateWithLifecycle()
    val notificationBadge = when {
        unreadCount <= 0 -> null
        unreadCount > 9 -> "9+"
        else -> unreadCount.toString()
    }

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }

    val versionName = remember(context) { readAppVersionInfo(context).versionName }

    if (showLogoutDialog) {
        DestructiveActionDialog(
            title = stringResource(R.string.settings_logout_confirm_title),
            message = stringResource(R.string.settings_logout_confirm_message),
            confirmText = stringResource(R.string.settings_log_out),
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                showLogoutDialog = false
                viewModel.signOut()
            },
        )
    }

    if (showDeleteDialog) {
        DestructiveActionDialog(
            title = stringResource(R.string.settings_delete_confirm_title),
            message = stringResource(R.string.settings_delete_confirm_message),
            confirmText = stringResource(R.string.settings_confirm_delete),
            isLoading = isDeleting,
            onDismiss = { if (!isDeleting) showDeleteDialog = false },
            onConfirm = {
                isDeleting = true
                viewModel.revokeAccess()
                showDeleteDialog = false
                isDeleting = false
            },
        )
    }

    RecyclrDetailScaffold(
        title = stringResource(R.string.settings_title),
        onBack = onBack,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(RecyclrLayout.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(RecyclrLayout.SectionSpacing),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                SettingsSectionBlock {
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
                SettingsSectionBlock {
                    SettingsSectionTitle(stringResource(R.string.settings_section_account))
                    SettingsCard {
                        Column {
                            if (isGuest) {
                                SettingsMenuItem(
                                    icon = Icons.AutoMirrored.Outlined.Login,
                                    title = stringResource(R.string.settings_sign_in_create_account),
                                    onClick = { viewModel.setGuestModeEnabled(false) },
                                    iconGradient = SettingsGradients.Account,
                                )
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            }
                            SettingsMenuItem(
                                icon = Icons.Outlined.Person,
                                title = stringResource(R.string.settings_personal_information),
                                subtitle = stringResource(R.string.settings_personal_info_subtitle),
                                onClick = onOpenPersonalInformation,
                                iconGradient = SettingsGradients.Account,
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            SettingsMenuItem(
                                icon = Icons.Outlined.Notifications,
                                title = stringResource(R.string.settings_notifications_in_app),
                                subtitle = if (unreadCount > 0) {
                                    stringResource(R.string.settings_notifications_unread, unreadCount)
                                } else {
                                    null
                                },
                                badge = notificationBadge,
                                onClick = {
                                    viewModel.requestOpenHomeAndNotifications()
                                    onBack()
                                },
                                iconGradient = SettingsGradients.Notifications,
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            SettingsMenuItem(
                                icon = Icons.Outlined.Security,
                                title = stringResource(R.string.settings_security_privacy),
                                onClick = { },
                                iconGradient = SettingsGradients.Security,
                            )
                        }
                    }
                }
            }

            item {
                SettingsSectionBlock {
                    SettingsSectionTitle(stringResource(R.string.settings_section_app_info))
                    SettingsCard {
                        Column {
                            SettingsMenuItem(
                                icon = Icons.AutoMirrored.Outlined.HelpOutline,
                                title = stringResource(R.string.settings_help_center),
                                onClick = onOpenHelpCenter,
                                iconGradient = SettingsGradients.Help,
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            SettingsMenuItem(
                                icon = Icons.Outlined.Info,
                                title = stringResource(R.string.settings_about_app),
                                subtitle = stringResource(R.string.settings_about_subtitle, versionName),
                                onClick = onOpenAbout,
                                iconGradient = SettingsGradients.Info,
                            )
                        }
                    }
                }
            }

            if (!isGuest) {
                item {
                    SettingsSectionBlock {
                        SettingsSectionTitle(stringResource(R.string.settings_section_session))
                        SettingsCard {
                            Column {
                                SettingsMenuItem(
                                    icon = Icons.AutoMirrored.Outlined.Logout,
                                    title = stringResource(R.string.settings_log_out),
                                    onClick = { showLogoutDialog = true },
                                    iconGradient = SettingsGradients.Logout,
                                )
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                                SettingsMenuItem(
                                    icon = Icons.Outlined.DeleteOutline,
                                    title = stringResource(R.string.settings_delete_account),
                                    onClick = { showDeleteDialog = true },
                                    titleColor = MaterialTheme.colorScheme.error,
                                    iconGradient = SettingsGradients.Delete,
                                )
                            }
                        }
                    }
                }
            }

            item {
                SettingsSectionBlock {
                    Spacer(modifier = Modifier.height(8.dp))
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
private fun SettingsSectionBlock(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.recyclrContentWidth(),
        content = content,
    )
}
