package com.gdsc.recyclr.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gdsc.recyclr.components.design.RecyclrFeatureScaffold
import com.gdsc.recyclr.domain.model.Response

@Composable
fun AdminDashboardScreen(
    onBack: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel(),
) {
    val stats = (viewModel.statsResponse as? Response.Success)?.data ?: emptyMap()

    RecyclrFeatureScaffold(title = "Admin Dashboard", onBack = onBack) {
        Text("Overview", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AdminStatCard(
                label = "Total Users",
                value = stats["totalUsers"]?.toString() ?: "—",
                icon = Icons.Default.Group,
                modifier = Modifier.weight(1f),
            )
            AdminStatCard(
                label = "New Scans",
                value = stats["totalScans"]?.toString() ?: "—",
                icon = Icons.Default.Summarize,
                modifier = Modifier.weight(1f),
            )
        }

        Text("Moderation", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        AdminActionItem(
            icon = Icons.Default.Report,
            title = "Illegal Dumping Reports",
            subtitle = "${stats["pendingReports"] ?: 0} pending reviews",
            onClick = {},
        )

        AdminActionItem(
            icon = Icons.Default.Settings,
            title = "App Configuration",
            subtitle = "Challenges, rewards, etc.",
            onClick = {},
        )
    }
}

@Composable
private fun AdminStatCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
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
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth(),
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
