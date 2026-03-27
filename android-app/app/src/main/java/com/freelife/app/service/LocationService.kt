package com.freelife.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.freelife.app.network.LocationHubClient

class LocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var hubClient: LocationHubClient? = null
    private var currentGroupId: Int = -1

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val EXTRA_GROUP_ID = "EXTRA_GROUP_ID"
        const val EXTRA_TOKEN = "EXTRA_TOKEN"
        const val NOTIF_ID = 1001
        const val CHANNEL_ID = "location_service_channel"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                currentGroupId = intent.getIntExtra(EXTRA_GROUP_ID, -1)
                val token = intent.getStringExtra(EXTRA_TOKEN) ?: return START_NOT_STICKY
                createNotificationChannel()
                startForeground(NOTIF_ID, buildNotification())
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                startLocationUpdates()
                connectSignalR(token)
            }
            ACTION_STOP -> stopSelf()
        }
        return START_STICKY
    }

    @Suppress("MissingPermission")
    private fun startLocationUpdates() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10_000L)
            .setMinUpdateIntervalMillis(5_000L)
            .build()

        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let { location ->
                hubClient?.sendLocation(currentGroupId, location.latitude, location.longitude)
            }
        }
    }

    private fun connectSignalR(token: String) {
        Thread {
            try {
                hubClient = LocationHubClient(token).apply {
                    connect()
                    joinGroup(currentGroupId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun buildNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("FreeLife Active")
        .setContentText("Sharing your location with your group")
        .setSmallIcon(android.R.drawable.ic_menu_mylocation)
        .build()

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Location Sharing",
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        hubClient?.disconnect()
        super.onDestroy()
    }
}
