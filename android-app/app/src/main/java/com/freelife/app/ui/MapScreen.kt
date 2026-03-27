package com.freelife.app.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.freelife.app.viewmodel.MapViewModel

private val cebuCity = LatLng(10.3157, 123.8854)

@Composable
fun MapScreen(navController: NavController, groupId: Int) {
    val viewModel: MapViewModel = viewModel()
    val memberLocations by viewModel.memberLocations.collectAsState()
    val memberNames by viewModel.memberNames.collectAsState()
    val myLocation by viewModel.myLocation.collectAsState()

    LaunchedEffect(groupId) {
        viewModel.startTracking(groupId)
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.stopTracking() }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cebuCity, 14f)
    }

    RequestLocationPermission {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            // Other group members' pins
            memberLocations.forEach { (userId, latLng) ->
                val displayName = memberNames[userId] ?: userId
                Marker(
                    state = MarkerState(position = latLng),
                    title = displayName,
                    snippet = "Last updated just now"
                )
            }

            // "You are here" blue pin
            myLocation?.let { loc ->
                Marker(
                    state = MarkerState(position = loc),
                    title = "You",
                    snippet = "Your current location",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )
            }
        }
    }
}
