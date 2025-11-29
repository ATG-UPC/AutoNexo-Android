package com.atg.autonexo.features.matching.data.remote.models

import com.atg.autonexo.features.matching.domain.models.ServiceCatalog
import com.atg.autonexo.features.matching.domain.models.RequestStatus
import com.google.gson.annotations.SerializedName

data class RequestResponseDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("userId")
    val userId: Long,
    @SerializedName("vehicleId")
    val vehicleId: Long,
    @SerializedName("requestedServices")
    val requestedServices: List<ServiceCatalog>,
    @SerializedName("description")
    val description: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("searchRadiusKm")
    val searchRadiusKm: Int,
    @SerializedName("status")
    val status: RequestStatus,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("canceledAt")
    val canceledAt: String?
)