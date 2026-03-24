package com.freelife.app.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

private val cebuCity = LatLng(10.3157, 123.8854)

@Composable
fun MapScreen(navController: NavController, groupId: Int) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cebuCity, 14f)
    }
    val markerState = remember { MarkerState(position = cebuCity) }

    RequestLocationPermission {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = markerState,
                title = "Group $groupId",
                snippet = "Tracking location in Cebu City"
            )
        }
    }
}
