package com.freelife.app.model

data class UpdateLocationRequest(
    val latitude: Double,
    val longitude: Double
)

data class LocationResponse(
    val userId: Int,
    val userName: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: String
)
