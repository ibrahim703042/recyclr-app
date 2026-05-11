package com.gdsc.recyclr.components.composable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecyclrTopBar(
    title: String,
    onSignOut: () -> Unit = {},
    onDeleteAccount: () -> Unit = {},
    showMenu: Boolean = true
) {
    var menuExpanded by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        actions = {
            if (showMenu) {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = "Menu")
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Sign out") },
                        onClick = {
                            menuExpanded = false
                            onSignOut()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete account") },
                        onClick = {
                            menuExpanded = false
                            onDeleteAccount()
                        }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicTopBar(title: String) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        }
    )
}
