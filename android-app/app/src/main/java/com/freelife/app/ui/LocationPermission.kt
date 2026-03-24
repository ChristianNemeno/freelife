package com.freelife.app.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

private val locationPermissions = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

@Composable
fun RequestLocationPermission(onGranted: @Composable () -> Unit) {
    val context = LocalContext.current
    var hasRequestedPermissions by rememberSaveable { mutableStateOf(false) }
    var permissionsGranted by remember { mutableStateOf(hasLocationPermissions(context)) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        permissionsGranted = hasLocationPermissions(context)
    }

    LaunchedEffect(permissionsGranted, hasRequestedPermissions) {
        if (!permissionsGranted && !hasRequestedPermissions) {
            hasRequestedPermissions = true
            permissionLauncher.launch(locationPermissions)
        }
    }

    if (permissionsGranted) {
        onGranted()
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Location permission is required to use UserTrack.")
        }
    }
}

private fun hasLocationPermissions(context: Context): Boolean {
    return locationPermissions.all { permission ->
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}
