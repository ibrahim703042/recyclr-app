package com.gdsc.recyclr.components.design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gdsc.recyclr.components.composable.BasicTopBar

/** Shared layout tokens for detail / settings / dashboard screens. */
object RecyclrLayout {
    val ContentMaxWidth = 600.dp
    val DialogMaxWidth = 480.dp
    val DialogWidthFraction = 0.92f
    val ChatBubbleMaxWidth = 280.dp
    val ScreenPadding = 20.dp
    val SectionSpacing = 16.dp
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecyclrDetailScaffold(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState? = null,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = { BasicTopBar(title = title, onBack = onBack) },
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = snackbarHostState?.let { { SnackbarHost(it) } } ?: {},
        content = content,
    )
}

/** Centers content and caps width on tablets / foldables. */
@Composable
fun RecyclrWidthContainer(
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = RecyclrLayout.ScreenPadding,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = RecyclrLayout.ContentMaxWidth)
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding),
            content = content,
        )
    }
}

@Composable
fun RecyclrScrollScreen(
    title: String,
    onBack: () -> Unit,
    verticalSpacing: Dp = 24.dp,
    snackbarHostState: SnackbarHostState? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scroll = rememberScrollState()
    RecyclrDetailScaffold(
        title = title,
        onBack = onBack,
        snackbarHostState = snackbarHostState,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scroll),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            RecyclrWidthContainer {
                Column(
                    verticalArrangement = Arrangement.spacedBy(verticalSpacing),
                    content = content,
                )
            }
        }
    }
}

/** Standard scroll scaffold for feature screens (challenges, wallet, admin, etc.). */
@Composable
fun RecyclrFeatureScaffold(
    title: String,
    onBack: () -> Unit,
    verticalSpacing: Dp = RecyclrLayout.SectionSpacing,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scroll = rememberScrollState()
    RecyclrDetailScaffold(title = title, onBack = onBack) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scroll),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            RecyclrWidthContainer {
                Column(
                    verticalArrangement = Arrangement.spacedBy(verticalSpacing),
                    content = content,
                )
            }
        }
    }
}

fun Modifier.recyclrContentWidth(): Modifier = this
    .widthIn(max = RecyclrLayout.ContentMaxWidth)
    .fillMaxWidth()
