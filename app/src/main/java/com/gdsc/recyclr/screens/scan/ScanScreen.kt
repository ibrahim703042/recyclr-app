package com.gdsc.recyclr.screens.scan

import android.Manifest
import android.content.Context
import android.graphics.BitmapFactory
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.gdsc.recyclr.domain.model.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.Executor

@Composable
fun ScanScreen(
    viewModel: ScanViewModel = hiltViewModel(),
    navigateToResults: (itemType: String, points: Int, co2SavedGrams: Float, destination: String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val executor: Executor = remember { ContextCompat.getMainExecutor(context) }

    var hasCameraPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    var showManualEntry by remember { mutableStateOf(false) }
    var pendingHazardousResult by remember { mutableStateOf<ScanResult?>(null) }
    var capturing by remember { mutableStateOf(false) }

    val submitFailure = (viewModel.submitResponse as? Response.Failure)?.e
    LaunchedEffect(submitFailure) {
        val ex = submitFailure ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(ex.message ?: "Échec de l'enregistrement du scan")
        viewModel.acknowledgeSubmitFailure()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (hasCameraPermission) {
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    context = context,
                    imageCapture = imageCapture
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "L'autorisation caméra est nécessaire pour scanner.")
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(MaterialTheme.colors.surface.copy(alpha = 0.9f))
                    .padding(12.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            if (capturing) return@Button
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
                                                snackbarHostState.showSnackbar("Impossible de lire la photo")
                                            }
                                        }
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        capturing = false
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                exception.message ?: "Erreur de capture"
                                            )
                                        }
                                    }
                                }
                            )
                        },
                        enabled = hasCameraPermission && !capturing &&
                            viewModel.submitResponse !is Response.Loading
                    ) {
                        Text(if (capturing) "Capture…" else "Scanner la vue")
                    }
                    OutlinedButton(
                        onClick = { showManualEntry = true },
                        enabled = viewModel.submitResponse !is Response.Loading
                    ) {
                        Text("Saisie manuelle")
                    }
                }
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
                            if (result.isHazardous) {
                                pendingHazardousResult = result
                                viewModel.acknowledgeSubmitSuccess()
                            } else {
                                navigateToResults(
                                    result.itemType,
                                    result.points,
                                    result.co2SavedGrams,
                                    result.destination
                                )
                                viewModel.acknowledgeSubmitSuccess()
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
            title = { Text("Type d'article") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { viewModel.submitManualScan("Plastic"); showManualEntry = false }) { Text("Plastic") }
                    OutlinedButton(onClick = { viewModel.submitManualScan("Paper"); showManualEntry = false }) { Text("Paper") }
                    OutlinedButton(onClick = { viewModel.submitManualScan("Furniture"); showManualEntry = false }) { Text("Furniture") }
                    OutlinedButton(onClick = { viewModel.submitManualScan("Hazardous"); showManualEntry = false }) { Text("Hazardous") }
                }
            },
            confirmButton = {},
            dismissButton = {
                OutlinedButton(onClick = { showManualEntry = false }) { Text("Annuler") }
            }
        )
    }

    val hazard = pendingHazardousResult
    if (hazard != null) {
        AlertDialog(
            onDismissRequest = { pendingHazardousResult = null },
            title = { Text("Article dangereux") },
            text = {
                Text("Piles / déchets électroniques : ne pas les mettre au recyclage classique. Déposez-les uniquement en point collecte spécialisé.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        pendingHazardousResult = null
                        navigateToResults(hazard.itemType, hazard.points, hazard.co2SavedGrams, hazard.destination)
                    }
                ) { Text("Continuer") }
            },
            dismissButton = {
                OutlinedButton(onClick = { pendingHazardousResult = null }) { Text("Annuler") }
            }
        )
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
            ContextCompat.getMainExecutor(this)
        )
    }

@Composable
private fun CameraPreview(
    modifier: Modifier,
    context: Context,
    imageCapture: ImageCapture
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
                imageCapture
            )
            awaitCancellation()
        } finally {
            cameraProvider.unbindAll()
        }
    }

    AndroidView(factory = { previewView }, modifier = modifier)
}
