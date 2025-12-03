package com.atg.autonexo.features.matching.data.remote.models

import com.atg.autonexo.features.matching.domain.models.RequestStatus
import com.google.gson.annotations.SerializedName

data class RequestResponseDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("vehicleId")
    val vehicleId: Long,
    @SerializedName("requestedServices")
    val requestedServices: List<String>,
    @SerializedName("description")
    val description: String,
    @SerializedName("matchScore")
    val matchScore: Double,
    @SerializedName("matchingServices")
    val matchingServices: List<String>,
    @SerializedName("userLocation")
    val userLocation: UserLocationDto,
    @SerializedName("distanceKm")
    val distanceKm: Double,
    @SerializedName("status")
    val status: RequestStatus,
    @SerializedName("createdAt")
    val createdAt: String
)
data class UserLocationDto(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double
)
