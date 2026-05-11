package com.gdsc.recyclr.screens.results

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.preferences.ThemeToggleIconButton
import com.gdsc.recyclr.ui.theme.LeafGreen
import com.gdsc.recyclr.ui.theme.recyclrCardBackground
import com.gdsc.recyclr.ui.theme.recyclrScreenBackground

@Composable
fun ResultsScreen(
    itemType: String,
    points: Int,
    co2SavedGrams: Float,
    destination: String,
    onRecycleAgain: () -> Unit = {},
    onViewRewards: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(recyclrScreenBackground()),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier.padding(20.dp),
            shape = RoundedCornerShape(20.dp),
            backgroundColor = recyclrCardBackground(),
            elevation = 0.dp,
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ThemeToggleIconButton()
                Text(text = stringResource(R.string.results_title), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = stringResource(R.string.results_scanned, itemType.lowercase()))
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(R.string.results_points, points), fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(R.string.results_co2, co2SavedGrams.toString()))
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(R.string.results_destination, destination))
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onRecycleAgain,
                    colors = ButtonDefaults.buttonColors(backgroundColor = LeafGreen),
                ) { Text(stringResource(R.string.results_recycle_again), color = Color.White) }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(onClick = onViewRewards) { Text(stringResource(R.string.results_view_rewards)) }
            }
        }
    }
}
