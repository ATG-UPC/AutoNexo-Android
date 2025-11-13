package com.atg.autonexo.features.matchingbooking.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * DTO para Service Request - Coincide con ServiceRequestResource del backend
 */
data class ServiceRequestDto(
    @SerializedName("id")
    val id: Long?,
    
    @SerializedName("userId")
    val userId: Long?,
    
    @SerializedName("vehicleId")
    val vehicleId: Long?,
    
    @SerializedName("requestedServices")
    val requestedServices: List<String>?,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("latitude")
    val latitude: Double?,
    
    @SerializedName("longitude")
    val longitude: Double?,
    
    @SerializedName("searchRadiusKm")
    val searchRadiusKm: Int?,
    
    @SerializedName("status")
    val status: String?,
    
    @SerializedName("createdAt")
    val createdAt: String?,
    
    @SerializedName("cancelledAt")
    val cancelledAt: String?
)
