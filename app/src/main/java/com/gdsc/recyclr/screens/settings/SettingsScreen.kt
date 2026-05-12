package com.gdsc.recyclr.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gdsc.recyclr.activities.MainViewModel
import com.gdsc.recyclr.components.composable.BasicTopBar
import com.gdsc.recyclr.components.preferences.LanguageSelectorRow
import com.gdsc.recyclr.components.preferences.ThemeModeSelectorRow

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            BasicTopBar(
                title = "Settings",
                onBack = onBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(modifier = Modifier.widthIn(max = 600.dp)) {
                    SettingsSectionTitle("Preferences")
                    SettingsCard {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            ThemeModeSelectorRow()
                            HorizontalDivider(modifier = Modifier.alpha(0.5f))
                            LanguageSelectorRow()
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.widthIn(max = 600.dp)) {
                    SettingsSectionTitle("Account")
                    SettingsCard {
                        Column {
                            SettingsMenuItem(
                                icon = Icons.Outlined.Person,
                                title = "Personal Information",
                                onClick = { /* TODO */ }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            SettingsMenuItem(
                                icon = Icons.Outlined.Notifications,
                                title = "Notifications",
                                onClick = { /* TODO */ }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            SettingsMenuItem(
                                icon = Icons.Outlined.Security,
                                title = "Security & Privacy",
                                onClick = { /* TODO */ }
                            )
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.widthIn(max = 600.dp)) {
                    SettingsSectionTitle("App Info")
                    SettingsCard {
                        Column {
                            SettingsMenuItem(
                                icon = Icons.Outlined.HelpOutline,
                                title = "Help Center",
                                onClick = { /* TODO */ }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            SettingsMenuItem(
                                icon = Icons.Outlined.Info,
                                title = "About Recyclr",
                                onClick = { /* TODO */ }
                            )
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.widthIn(max = 600.dp)) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { /* TODO: Sign out logic */ },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text("Log Out", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    TextButton(
                        onClick = { /* TODO: Delete Account */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete Account")
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Version 1.0.0 (Build 123)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
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
    onClick: () -> Unit
) {
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
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline
        )
    }
}
