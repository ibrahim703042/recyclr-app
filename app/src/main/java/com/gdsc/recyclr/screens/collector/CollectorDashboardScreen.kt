package com.gdsc.recyclr.screens.collector

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gdsc.recyclr.components.design.RecyclrFeatureScaffold

@Composable
fun CollectorDashboardScreen(onBack: () -> Unit) {
    RecyclrFeatureScaffold(title = "Collector Dashboard", onBack = onBack) {
        Text("Assigned Pickups", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        PickupTaskItem(
            address = "Kamenge, Bujumbura",
            items = "Plastic, Paper",
            weight = "15kg",
            status = "Pending",
        )

        PickupTaskItem(
            address = "Ngagara, Q3",
            items = "Metal",
            weight = "8kg",
            status = "In Progress",
        )

        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = MaterialTheme.shapes.large,
        ) {
            androidx.compose.material3.Icon(imageVector = Icons.Default.Map, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("View Pickup Map")
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
