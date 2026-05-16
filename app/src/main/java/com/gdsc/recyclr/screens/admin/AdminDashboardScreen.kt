package com.gdsc.recyclr.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gdsc.recyclr.components.composable.BasicTopBar
import com.gdsc.recyclr.domain.model.Response

@Composable
fun AdminDashboardScreen(
    onBack: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val stats = (viewModel.statsResponse as? Response.Success)?.data ?: emptyMap()
    
    Scaffold(
        topBar = { BasicTopBar(title = "Admin Dashboard", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Overview", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AdminStatCard(
                    label = "Total Users", 
                    value = stats["totalUsers"]?.toString() ?: "—", 
                    icon = Icons.Default.Group, 
                    modifier = Modifier.weight(1f)
                )
                AdminStatCard(
                    label = "New Scans", 
                    value = stats["totalScans"]?.toString() ?: "—", 
                    icon = Icons.Default.Summarize, 
                    modifier = Modifier.weight(1f)
                )
            }
            
            Text("Moderation", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            
            AdminActionItem(
                icon = Icons.Default.Report,
                title = "Illegal Dumping Reports",
                subtitle = "${stats["pendingReports"] ?: 0} pending reviews",
                onClick = {}
            )
            
            AdminActionItem(
                icon = Icons.Default.Settings,
                title = "App Configuration",
                subtitle = "Challenges, rewards, etc.",
                onClick = {}
            )
        }
    }
}

@Composable
private fun AdminStatCard(label: String, value: String, icon: ImageVector, modifier: Modifier) {
    ElevatedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
            Text(text = label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun AdminActionItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
