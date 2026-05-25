package com.gdsc.recyclr.screens.results

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.results.CelebrationConfetti

@Composable
fun ResultsScreen(
    itemType: String,
    points: Int,
    co2SavedGrams: Float,
    destination: String,
    onRecycleAgain: () -> Unit = {},
    onViewRewards: () -> Unit = {},
    onOpenMap: () -> Unit = {},
) {
    val context = LocalContext.current
    val scroll = rememberScrollState()
    val co2Kg = co2SavedGrams / 1000f
    val accentPoints = MaterialTheme.colorScheme.primary
    val accentCo2 = MaterialTheme.colorScheme.tertiary

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scroll)
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.recycle),
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.results_success_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.results_identified_label, itemType),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp),
        )

        Spacer(modifier = Modifier.height(20.dp))

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    ResultStat(
                        label = stringResource(R.string.results_stat_points_label),
                        value = "+$points",
                        color = accentPoints,
                    )
                    ResultStat(
                        label = stringResource(R.string.results_stat_co2_label),
                        value = String.format("%.2f kg", co2Kg),
                        color = accentCo2,
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                Text(
                    text = stringResource(R.string.results_disposal_title),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = stringResource(R.string.results_disposal_body, itemType),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            onClick = onOpenMap,
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.results_nearest_point),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = stringResource(
                            R.string.results_map_subtitle,
                            stringResource(R.string.results_distance_approx),
                            destination,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            if (maxWidth < 360.dp) {
                ResultsActionsStacked(
                    itemType = itemType,
                    points = points,
                    co2SavedGrams = co2SavedGrams,
                    context = context,
                    onRecycleAgain = onRecycleAgain,
                )
            } else {
                ResultsActionsWide(
                    itemType = itemType,
                    points = points,
                    co2SavedGrams = co2SavedGrams,
                    context = context,
                    onRecycleAgain = onRecycleAgain,
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
        
        // Celebration confetti overlay
        CelebrationConfetti(modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun ResultsActionsWide(
    itemType: String,
    points: Int,
    co2SavedGrams: Float,
    context: Context,
    onRecycleAgain: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        ResultsVerifyButton(Modifier.fillMaxWidth())
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            ResultsDoneButton(Modifier.weight(1f), onRecycleAgain)
            ResultsShareButton(
                modifier = Modifier.weight(1f),
                itemType = itemType,
                points = points,
                co2SavedGrams = co2SavedGrams,
                context = context,
            )
        }
    }
}

@Composable
private fun ResultsActionsStacked(
    itemType: String,
    points: Int,
    co2SavedGrams: Float,
    context: Context,
    onRecycleAgain: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        ResultsVerifyButton(Modifier.fillMaxWidth())
        ResultsDoneButton(Modifier.fillMaxWidth(), onRecycleAgain)
        ResultsShareButton(
            modifier = Modifier.fillMaxWidth(),
            itemType = itemType,
            points = points,
            co2SavedGrams = co2SavedGrams,
            context = context,
        )
    }
}

@Composable
private fun ResultsVerifyButton(modifier: Modifier = Modifier) {
    Button(
        onClick = { /* TODO: Verification logic */ },
        modifier = modifier.height(52.dp),
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
        ),
    ) {
        Icon(imageVector = Icons.Default.Check, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(
            stringResource(R.string.results_verify_double),
            fontWeight = FontWeight.Bold,
            maxLines = 2,
        )
    }
}

@Composable
private fun ResultsDoneButton(modifier: Modifier = Modifier, onRecycleAgain: () -> Unit) {
    Button(
        onClick = onRecycleAgain,
        modifier = modifier.height(52.dp),
        shape = MaterialTheme.shapes.large,
    ) {
        Text(stringResource(R.string.results_done))
    }
}

@Composable
private fun ResultsShareButton(
    modifier: Modifier = Modifier,
    itemType: String,
    points: Int,
    co2SavedGrams: Float,
    context: Context,
) {
    OutlinedButton(
        onClick = {
            val co2Kg = co2SavedGrams / 1000f
            val text = context.getString(
                R.string.results_share_text,
                itemType,
                points,
                co2Kg,
            )
            val send = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            context.startActivity(Intent.createChooser(send, context.getString(R.string.results_share_title)))
        },
        modifier = modifier.height(52.dp),
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        Icon(imageVector = Icons.Default.Share, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(stringResource(R.string.results_share))
    }
}

@Composable
private fun ResultStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black,
            color = color,
            textAlign = TextAlign.Center,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
