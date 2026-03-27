package com.freelife.app.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.freelife.app.network.LocationHubClient
import com.freelife.app.repository.GroupRepository
import com.freelife.app.repository.TokenRepository
import com.freelife.app.service.LocationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenRepository = TokenRepository(application)
    private val groupRepository = GroupRepository(application)
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

    private val _memberLocations = MutableStateFlow<Map<String, LatLng>>(emptyMap())
    val memberLocations: StateFlow<Map<String, LatLng>> = _memberLocations.asStateFlow()

    // userId (string) -> display name
    private val _memberNames = MutableStateFlow<Map<String, String>>(emptyMap())
    val memberNames: StateFlow<Map<String, String>> = _memberNames.asStateFlow()

    private val _myLocation = MutableStateFlow<LatLng?>(null)
    val myLocation: StateFlow<LatLng?> = _myLocation.asStateFlow()

    private var hubClient: LocationHubClient? = null

    private val myLocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let { loc ->
                _myLocation.value = LatLng(loc.latitude, loc.longitude)
            }
        }
    }

    @Suppress("MissingPermission")
    fun startTracking(groupId: Int) {
        val token = tokenRepository.getToken() ?: return

        // Fetch member names for display on map pins
        viewModelScope.launch {
            groupRepository.getGroupMembers(groupId).onSuccess { members ->
                _memberNames.value = members.associate { it.userId.toString() to it.name }
            }
        }

        // Connect SignalR hub
        viewModelScope.launch(Dispatchers.IO) {
            try {
                hubClient = LocationHubClient(token).apply {
                    onLocationReceived = { userId, lat, lng ->
                        _memberLocations.value = _memberLocations.value + (userId to LatLng(lat, lng))
                    }
                    connect()
                    joinGroup(groupId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Track own location for "You are here" marker
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5_000L)
            .setMinUpdateIntervalMillis(3_000L)
            .build()
        fusedLocationClient.requestLocationUpdates(locationRequest, myLocationCallback, Looper.getMainLooper())

        // Start background service to broadcast location to group
        val intent = Intent(getApplication(), LocationService::class.java).apply {
            action = LocationService.ACTION_START
            putExtra(LocationService.EXTRA_GROUP_ID, groupId)
            putExtra(LocationService.EXTRA_TOKEN, token)
        }
        getApplication<Application>().startForegroundService(intent)
    }

    fun stopTracking() {
        hubClient?.disconnect()
        hubClient = null
        fusedLocationClient.removeLocationUpdates(myLocationCallback)
        val intent = Intent(getApplication(), LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
        }
        getApplication<Application>().startService(intent)
    }

    override fun onCleared() {
        stopTracking()
        super.onCleared()
    }
}
