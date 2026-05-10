package com.gdsc.recyclr.screens.results

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ResultsScreen(
    itemType: String,
    points: Int,
    co2SavedGrams: Float,
    destination: String,
    onRecycleAgain: () -> Unit = {},
    onViewRewards: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Item: $itemType")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Points earned: +$points")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "CO₂ saved: ${co2SavedGrams} g")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Destination: $destination")
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRecycleAgain) { Text("Recycle again") }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(onClick = onViewRewards) { Text("View rewards") }
        }
    }
}

