package com.gdsc.recyclr.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gdsc.recyclr.components.composable.BasicTopBar

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    Scaffold(
        topBar = { BasicTopBar("Welcome") }
    ) { padding ->
        OnboardingContent(padding = padding, onFinish = onFinish)
    }
}

@Composable
private fun OnboardingContent(
    padding: PaddingValues,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Recyclr",
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Scan waste, earn rewards, find nearby collection points.")
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onFinish) {
            Text(text = "Get started")
        }
    }
}

