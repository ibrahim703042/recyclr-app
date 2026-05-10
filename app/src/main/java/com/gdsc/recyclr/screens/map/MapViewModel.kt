package com.gdsc.recyclr.screens.map

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdsc.recyclr.domain.model.CollectionPoint
import com.gdsc.recyclr.domain.model.Response
import com.gdsc.recyclr.domain.model.Response.Loading
import com.gdsc.recyclr.domain.repository.CollectionPointsRepository
import com.gdsc.recyclr.data.location.GeofenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: CollectionPointsRepository,
    private val geofenceManager: GeofenceManager
) : ViewModel() {

    var pointsResponse: Response<List<CollectionPoint>> by mutableStateOf(Loading)
        private set

    var selectedTypes: Set<String> by mutableStateOf(emptySet())
        private set

    var radiusKm: Double by mutableStateOf(5.0)
        private set

    /** Position utilisateur (GPS). Si null, on utilise le point par défaut (BU). */
    var userLatLng: Pair<Double, Double>? by mutableStateOf(null)
        private set

    private val defaultLat = -3.361260
    private val defaultLng = 29.347916

    private val queryLat: Double get() = userLatLng?.first ?: defaultLat
    private val queryLng: Double get() = userLatLng?.second ?: defaultLng

    init {
        refresh()
    }

    fun setLiveUserLocation(lat: Double, lng: Double) {
        userLatLng = lat to lng
        refresh()
    }

    fun toggleType(type: String) {
        selectedTypes = if (type in selectedTypes) selectedTypes - type else selectedTypes + type
        refresh()
    }

    fun applyRadiusKm(km: Double) {
        radiusKm = km
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            pointsResponse = Loading
            val resp = repository.getNearbyCollectionPoints(
                lat = queryLat,
                lng = queryLng,
                radiusKm = radiusKm,
                typeFilter = selectedTypes
            )
            pointsResponse = resp
            if (resp is Response.Success) {
                geofenceManager.register(resp.data.orEmpty())
            }
        }
    }
}
