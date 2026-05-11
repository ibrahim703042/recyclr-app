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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.gdsc.recyclr.ui.theme.LeafGreen
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
    var pendingHazardousResult by remember { mutableStateOf<ScanResult?>(null) }
    var capturing by remember { mutableStateOf(false) }
    var inlineResult by remember { mutableStateOf<ScanResult?>(null) }

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

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 48.dp, vertical = 120.dp),
                contentAlignment = Alignment.Center,
            ) {
                ScanViewfinder()
            }

            IconButton(
                onClick = { captureAndScan() },
                enabled = hasCameraPermission && !capturing && viewModel.submitResponse !is Response.Loading,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 28.dp)
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color.White),
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Scan",
                    tint = Color.Black,
                    modifier = Modifier.size(30.dp),
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface.copy(alpha = 0.92f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(modifier = Modifier.size(48.dp))
                Text(text = stringResource(R.string.scan_title), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                ThemeToggleIconButton()
            }

            inlineResult?.let { result ->
                ScanResultBanner(
                    itemType = result.itemType,
                    points = result.points,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(start = 16.dp, end = 16.dp, bottom = 110.dp),
                )
            }

            when (val resp = viewModel.submitResponse) {
                is Response.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = LeafGreen)
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

    if (showManualEntry) {
        AlertDialog(
            onDismissRequest = { showManualEntry = false },
            title = { Text(stringResource(R.string.scan_manual_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { viewModel.submitManualScan("Plastic"); showManualEntry = false }) { Text(stringResource(R.string.scan_manual_plastic)) }
                    OutlinedButton(onClick = { viewModel.submitManualScan("Paper"); showManualEntry = false }) { Text(stringResource(R.string.scan_manual_paper)) }
                    OutlinedButton(onClick = { viewModel.submitManualScan("Furniture"); showManualEntry = false }) { Text(stringResource(R.string.scan_manual_furniture)) }
                    OutlinedButton(onClick = { viewModel.submitManualScan("Hazardous"); showManualEntry = false }) { Text(stringResource(R.string.scan_manual_hazardous)) }
                }
            },
            confirmButton = {},
            dismissButton = {
                OutlinedButton(onClick = { showManualEntry = false }) { Text(stringResource(R.string.scan_cancel)) }
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
                androidx.compose.material.Button(
                    onClick = {
                        pendingHazardousResult = null
                        navigateToResults(hazard.itemType, hazard.points, hazard.co2SavedGrams, hazard.destination)
                    },
                ) { Text(stringResource(R.string.scan_continue)) }
            },
            dismissButton = {
                OutlinedButton(onClick = { pendingHazardousResult = null }) { Text(stringResource(R.string.scan_cancel)) }
            },
        )
    }
}

@Composable
private fun ScanViewfinder() {
    val corner = 28.dp
    val stroke = 4.dp
    Box(modifier = Modifier.size(220.dp)) {
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
        shape = RoundedCornerShape(18.dp),
        backgroundColor = Color.White,
        elevation = 6.dp,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CategoryPlastic),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.LocalDrink,
                    contentDescription = null,
                    tint = Color(0xFF4A4A4A),
                )
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = stringResource(R.string.scan_result_message, itemType.lowercase()),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                )
                Text(
                    text = stringResource(R.string.scan_result_points, points),
                    fontSize = 13.sp,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
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
