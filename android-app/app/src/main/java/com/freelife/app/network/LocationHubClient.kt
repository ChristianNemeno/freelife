package com.freelife.app.network

import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import io.reactivex.rxjava3.core.Single

class LocationHubClient(private val token: String) {

    private lateinit var hubConnection: HubConnection

    var onLocationReceived: ((userId: String, lat: Double, lng: Double) -> Unit)? = null
    var onUserJoined: ((userId: String) -> Unit)? = null
    var onUserOffline: ((userId: String) -> Unit)? = null

    fun connect() {
        hubConnection = HubConnectionBuilder
            .create("http://34.126.112.84:8080/locationHub")
            .withAccessTokenProvider(Single.just(token))
            .build()

        hubConnection.on(
            "ReceiveLocation",
            { userId: String, lat: Double, lng: Double, _: String ->
                onLocationReceived?.invoke(userId, lat, lng)
            },
            String::class.java, Double::class.java,
            Double::class.java, String::class.java
        )

        hubConnection.on(
            "UserJoined",
            { userId: String -> onUserJoined?.invoke(userId) },
            String::class.java
        )

        hubConnection.on(
            "UserOffline",
            { userId: String -> onUserOffline?.invoke(userId) },
            String::class.java
        )

        hubConnection.start().blockingAwait()
    }

    fun joinGroup(groupId: Int) {
        if (::hubConnection.isInitialized &&
            hubConnection.connectionState == HubConnectionState.CONNECTED
        ) {
            hubConnection.invoke("JoinGroup", groupId.toString())
        }
    }

    fun sendLocation(groupId: Int, latitude: Double, longitude: Double) {
        if (::hubConnection.isInitialized &&
            hubConnection.connectionState == HubConnectionState.CONNECTED
        ) {
            hubConnection.invoke("SendLocation", groupId.toString(), latitude, longitude)
        }
    }

    fun disconnect() {
        if (::hubConnection.isInitialized) {
            hubConnection.stop()
        }
    }
}
