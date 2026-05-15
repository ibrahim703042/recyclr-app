package com.gdsc.recyclr.screens.collector

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gdsc.recyclr.components.composable.BasicTopBar

@Composable
fun CollectorDashboardScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = { BasicTopBar(title = "Collector Dashboard", onBack = onBack) }
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
            Text("Assigned Pickups", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            
            PickupTaskItem(
                address = "Kamenge, Bujumbura",
                items = "Plastic, Paper",
                weight = "15kg",
                status = "Pending"
            )
            
            PickupTaskItem(
                address = "Ngagara, Q3",
                items = "Metal",
                weight = "8kg",
                status = "In Progress"
            )
            
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Icon(imageVector = Icons.Default.Map, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("View Pickup Map")
            }
        }
    }
}

@Composable
private fun PickupTaskItem(address: String, items: String, weight: String, status: String) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = address, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Badge { Text(status) }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Items: $items", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Estimated: $weight", style = MaterialTheme.typography.bodySmall)
            
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = {}, modifier = Modifier.weight(1f)) { Text("Navigate") }
                OutlinedButton(onClick = {}, modifier = Modifier.weight(1f)) { Text("Complete") }
            }
        }
    }
}
