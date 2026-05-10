package com.gdsc.recyclr.screens.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedButton
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.gdsc.recyclr.domain.model.CollectionPoint
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.util.AppLogger
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val mapLoadError = (viewModel.pointsResponse as? Response.Failure)?.e?.message
    LaunchedEffect(mapLoadError) {
        if (mapLoadError != null) {
            snackbarHostState.showSnackbar("Points de collecte : $mapLoadError")
        }
    }

    val fallback = LatLng(-3.361260, 29.347916)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(fallback, 14f)
    }

    var locationGranted by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        locationGranted = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        if (fine || coarse) {
            locationGranted = true
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(locationGranted) {
        if (!locationGranted) return@LaunchedEffect
        runCatching {
            val fused = LocationServices.getFusedLocationProviderClient(context)
            val loc = fused.lastLocation.await()
            if (loc != null) {
                viewModel.setLiveUserLocation(loc.latitude, loc.longitude)
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(LatLng(loc.latitude, loc.longitude), 14f)
                )
            }
        }.onFailure {
            AppLogger.w("Position GPS indisponible — carte sur zone par défaut", it)
        }
    }

    LaunchedEffect(viewModel.userLatLng) {
        val loc = viewModel.userLatLng ?: return@LaunchedEffect
        runCatching {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(LatLng(loc.first, loc.second), 14f)
            )
        }.onFailure { AppLogger.w("Animation caméra carte", it) }
    }

    var selectedPoint by remember { mutableStateOf<CollectionPoint?>(null) }
    val sheetState = androidx.compose.material.rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetContent = {
                val p = selectedPoint
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = p?.name ?: "Point de collecte")
                    if (!p?.address.isNullOrBlank()) {
                        Text(text = p?.address.orEmpty())
                    }
                    Text(text = p?.hours ?: "")
                    Text(text = "Types : ${p?.types?.joinToString().orEmpty()}")
                    Text(text = "Tél. : ${p?.phone ?: "—"}")
                    p?.websiteUrl?.takeIf { it.isNotBlank() }?.let { Text(text = "Web : $it") }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TextButton(
                            onClick = {
                                val cp = selectedPoint ?: return@TextButton
                                runCatching {
                                    val uri = Uri.parse("google.navigation:q=${cp.lat},${cp.lng}")
                                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                }.onFailure {
                                    AppLogger.w("Directions", it)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Impossible d’ouvrir la navigation.")
                                    }
                                }
                            }
                        ) { Text("Itinéraire") }
                        TextButton(onClick = { scope.launch { sheetState.hide() } }) { Text("Fermer") }
                    }
                }
            }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = locationGranted),
                    uiSettings = MapUiSettings(myLocationButtonEnabled = locationGranted, zoomControlsEnabled = true)
                ) {
                    when (val resp = viewModel.pointsResponse) {
                        is Response.Success -> {
                            resp.data.orEmpty().forEach { p ->
                                Marker(
                                    state = MarkerState(position = LatLng(p.lat, p.lng)),
                                    title = p.name,
                                    snippet = p.hours,
                                    onClick = {
                                        selectedPoint = p
                                        scope.launch { sheetState.show() }
                                        true
                                    }
                                )
                            }
                        }
                        else -> Unit
                    }
                }

                Column(modifier = Modifier.padding(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { viewModel.applyRadiusKm(1.0) }) { Text("1 km") }
                        OutlinedButton(onClick = { viewModel.applyRadiusKm(5.0) }) { Text("5 km") }
                        OutlinedButton(onClick = { viewModel.applyRadiusKm(10.0) }) { Text("10 km") }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { viewModel.toggleType("plastic") }) { Text("Plastic") }
                        OutlinedButton(onClick = { viewModel.toggleType("paper") }) { Text("Paper") }
                        OutlinedButton(onClick = { viewModel.toggleType("hazardous") }) { Text("Hazardous") }
                    }
                }
            }
        }

        if (viewModel.pointsResponse is Response.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 48.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                CircularProgressIndicator()
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
