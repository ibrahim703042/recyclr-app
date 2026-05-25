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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.QrCodeScanner
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
import com.gdsc.recyclr.components.composable.BasicTopBar
import com.gdsc.recyclr.components.scan.ScanningFrame
import com.gdsc.recyclr.domain.model.Response
import java.io.File
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch

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
    var capturing by remember { mutableStateOf(false) }
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
        topBar = {
            BasicTopBar(title = stringResource(R.string.scan_title))
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        val controlsScroll = rememberScrollState()
        val outlineColors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
        val outlineBorder = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(Color.Black),
            ) {
                if (hasCameraPermission) {
                    CameraPreview(
                        modifier = Modifier.fillMaxSize(),
                        context = context,
                        imageCapture = imageCapture,
                    )
                    // Animated scanning frame overlay
                    ScanningFrame(
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = stringResource(R.string.scan_permission_required), color = Color.White)
                    }
                }
                
                // Flash and Gallery buttons
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = { /* TODO */ },
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.FlashOn, contentDescription = "Flash", tint = Color.White)
                    }
                    IconButton(
                        onClick = { /* TODO */ },
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Image, contentDescription = "Gallery", tint = Color.White)
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .background(MaterialTheme.colorScheme.background),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(controlsScroll)
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Button(
                        onClick = { captureAndScan() },
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp, max = 56.dp),
                        shape = MaterialTheme.shapes.large,
                        enabled = !capturing,
                    ) {
                        Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            stringResource(R.string.scan_action_capture),
                            maxLines = 2,
                        )
                    }
                    OutlinedButton(
                        onClick = { showBarcodeEntry = true },
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp, max = 56.dp),
                        shape = MaterialTheme.shapes.large,
                        colors = outlineColors,
                        border = outlineBorder,
                    ) {
                        Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            stringResource(R.string.scan_action_barcode),
                            maxLines = 2,
                        )
                    }
                }

                TextButton(onClick = { showManualEntry = true }) {
                    Text(
                        stringResource(R.string.scan_enter_manual),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.scan_recent_scans),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(listOf("Plastic Bottle", "Newspaper", "Glass Jar")) { item ->
                            RecentScanChip(item)
                        }
                    }
                }
                }
            }
        }
    }

    // Reuse existing dialogs for Barcode and Manual entry...
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
        val manualScroll = rememberScrollState()
        AlertDialog(
            onDismissRequest = { showManualEntry = false },
            title = { Text(stringResource(R.string.scan_manual_title)) },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = 360.dp)
                        .verticalScroll(manualScroll),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
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

    when (val resp = viewModel.submitResponse) {
        is Response.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is Response.Success -> {
            val result = resp.data
            LaunchedEffect(result) {
                if (result != null) {
                    viewModel.acknowledgeSubmitSuccess()
                    navigateToResults(
                        result.itemType,
                        result.points,
                        result.co2SavedGrams,
                        result.destination,
                    )
                }
            }
        }
        is Response.Failure -> Unit
    }
}

@Composable
private fun RecentScanChip(label: String) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        onClick = { /* TODO */ }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocalDrink,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun ScanViewfinderOverlay(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
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
