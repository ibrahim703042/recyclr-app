package com.gdsc.recyclr.screens.scan

import android.Manifest
import android.content.Context
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.gdsc.recyclr.R
import com.gdsc.recyclr.components.preferences.ThemeToggleIconButton
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.ui.theme.CategoryPlastic
import java.io.File
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    viewModel: ScanViewModel = hiltViewModel(),
    navigateToResults: (itemType: String, points: Int, co2SavedGrams: Float, destination: String) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val executor: Executor = remember { ContextCompat.getMainExecutor(context) }

    var hasCameraPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    var showManualEntry by remember { mutableStateOf(false) }
    var pendingHazardousResult by remember { mutableStateOf<ScanResult?>(null) }
    var capturing by remember { mutableStateOf(false) }
    var inlineResult by remember { mutableStateOf<ScanResult?>(null) }
    var showBarcodeEntry by remember { mutableStateOf(false) }
    var barcodeValue by remember { mutableStateOf("") }

    val submitFailure = (viewModel.submitResponse as? Response.Failure)?.e
    LaunchedEffect(submitFailure) {
        val ex = submitFailure ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(ex.message ?: context.getString(R.string.scan_save_failed))
        viewModel.acknowledgeSubmitFailure()
    }

    fun captureAndScan() {
        if (capturing) return
        capturing = true
        val file = File(context.cacheDir, "scan_${System.currentTimeMillis()}.jpg")
        val options = ImageCapture.OutputFileOptions.Builder(file).build()
        imageCapture.takePicture(
            options,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    capturing = false
                    val bmp = BitmapFactory.decodeFile(file.absolutePath)
                    file.delete()
                    if (bmp != null) {
                        viewModel.submitFromCapturedBitmap(bmp)
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar(context.getString(R.string.scan_capture_failed))
                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    capturing = false
                    scope.launch {
                        snackbarHostState.showSnackbar(exception.message ?: context.getString(R.string.scan_capture_error))
                    }
                }
            },
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            if (hasCameraPermission) {
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    context = context,
                    imageCapture = imageCapture,
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = stringResource(R.string.scan_permission_required))
                }
            }

            // Viewfinder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 48.dp, vertical = 120.dp),
                contentAlignment = Alignment.Center,
            ) {
                ScanViewfinder()
            }

            // Buttons over preview
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 120.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FilledTonalButton(onClick = { showManualEntry = true }) {
                    Text(stringResource(R.string.scan_manual_title))
                }
                FilledTonalButton(onClick = { showBarcodeEntry = true }) {
                    Text(stringResource(R.string.scan_barcode))
                }
            }

            // Capture button
            FloatingActionButton(
                onClick = { captureAndScan() },
                containerColor = Color.White,
                contentColor = Color.Black,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .size(72.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Scan",
                    modifier = Modifier.size(32.dp),
                )
            }

            // Header
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(modifier = Modifier.size(48.dp))
                Text(
                    text = stringResource(R.string.scan_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                ThemeToggleIconButton()
            }

            inlineResult?.let { result ->
                ScanResultBanner(
                    itemType = result.itemType,
                    points = result.points,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(start = 24.dp, end = 24.dp, bottom = 120.dp),
                )
            }

            when (val resp = viewModel.submitResponse) {
                is Response.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                is Response.Success -> {
                    val result = resp.data
                    LaunchedEffect(result) {
                        if (result != null) {
                            if (result.isHazardous) {
                                pendingHazardousResult = result
                                viewModel.acknowledgeSubmitSuccess()
                            } else {
                                inlineResult = result
                                viewModel.acknowledgeSubmitSuccess()
                                kotlinx.coroutines.delay(1800)
                                navigateToResults(
                                    result.itemType,
                                    result.points,
                                    result.co2SavedGrams,
                                    result.destination,
                                )
                                inlineResult = null
                            }
                        }
                    }
                }
                is Response.Failure -> Unit
            }
        }
    }

    if (showBarcodeEntry) {
        AlertDialog(
            onDismissRequest = { showBarcodeEntry = false },
            title = { Text(stringResource(R.string.scan_barcode)) },
            text = {
                OutlinedTextField(
                    value = barcodeValue,
                    onValueChange = { barcodeValue = it },
                    label = { Text(stringResource(R.string.scan_barcode_hint)) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.submitBarcodeScan(barcodeValue)
                        showBarcodeEntry = false
                        barcodeValue = ""
                    },
                ) { Text(stringResource(R.string.scan_continue)) }
            },
            dismissButton = {
                TextButton(onClick = { showBarcodeEntry = false }) {
                    Text(stringResource(R.string.scan_cancel))
                }
            },
        )
    }

    if (showManualEntry) {
        AlertDialog(
            onDismissRequest = { showManualEntry = false },
            title = { Text(stringResource(R.string.scan_manual_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val types = listOf(
                        "Plastic" to R.string.scan_manual_plastic,
                        "Paper" to R.string.scan_manual_paper,
                        "Furniture" to R.string.scan_manual_furniture,
                        "Hazardous" to R.string.scan_manual_hazardous
                    )
                    types.forEach { (type, resId) ->
                        FilledTonalButton(
                            onClick = { viewModel.submitManualScan(type); showManualEntry = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(resId))
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showManualEntry = false }) { Text(stringResource(R.string.scan_cancel)) }
            },
        )
    }

    val hazard = pendingHazardousResult
    if (hazard != null) {
        AlertDialog(
            onDismissRequest = { pendingHazardousResult = null },
            title = { Text(stringResource(R.string.scan_hazardous_title)) },
            text = {
                Text(stringResource(R.string.scan_hazardous_message))
            },
            confirmButton = {
                Button(
                    onClick = {
                        pendingHazardousResult = null
                        navigateToResults(hazard.itemType, hazard.points, hazard.co2SavedGrams, hazard.destination)
                    },
                ) { Text(stringResource(R.string.scan_continue)) }
            },
            dismissButton = {
                TextButton(onClick = { pendingHazardousResult = null }) { Text(stringResource(R.string.scan_cancel)) }
            },
        )
    }
}

@Composable
private fun ScanViewfinder() {
    val corner = 32.dp
    val stroke = 4.dp
    Box(modifier = Modifier.size(240.dp)) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .width(corner)
                .height(corner)
                .border(stroke, Color.White, RoundedCornerShape(topStart = 12.dp)),
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .width(corner)
                .height(corner)
                .border(stroke, Color.White, RoundedCornerShape(topEnd = 12.dp)),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .width(corner)
                .height(corner)
                .border(stroke, Color.White, RoundedCornerShape(bottomStart = 12.dp)),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .width(corner)
                .height(corner)
                .border(stroke, Color.White, RoundedCornerShape(bottomEnd = 12.dp)),
        )
    }
}

@Composable
private fun ScanResultBanner(
    itemType: String,
    points: Int,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = CategoryPlastic.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.LocalDrink,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = stringResource(R.string.scan_result_message, itemType.lowercase()),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(R.string.scan_result_points, points),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
            }
        }
    }
}

private suspend fun Context.awaitCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener(
            {
                try {
                    continuation.resume(future.get())
                } catch (e: Exception) {
                    continuation.resumeWithException(e)
                }
            },
            ContextCompat.getMainExecutor(this),
        )
    }

@Composable
private fun CameraPreview(
    modifier: Modifier,
    context: Context,
    imageCapture: ImageCapture,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    LaunchedEffect(lifecycleOwner, imageCapture) {
        val cameraProvider = context.awaitCameraProvider()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture,
            )
            awaitCancellation()
        } finally {
            cameraProvider.unbindAll()
        }
    }

    AndroidView(factory = { previewView }, modifier = modifier)
}
