package com.atg.autonexo.features.matchingbooking.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * DTO para Service Request
 */
data class ServiceRequestDto(
    @SerializedName("id")
    val id: Long?,
    
    @SerializedName("carOwnerId")
    val carOwnerId: Long?,
    
    @SerializedName("vehicleId")
    val vehicleId: Long?,
    
    @SerializedName("vehicleDescription")
    val vehicleDescription: String?,
    
    @SerializedName("serviceType")
    val serviceType: String?,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("urgencyLevel")
    val urgencyLevel: String?,
    
    @SerializedName("preferredDate")
    val preferredDate: String?,
    
    @SerializedName("latitude")
    val latitude: Double?,
    
    @SerializedName("longitude")
    val longitude: Double?,
    
    @SerializedName("status")
    val status: String?,
    
    @SerializedName("createdAt")
    val createdAt: String?,
    
    @SerializedName("offerCount")
    val offerCount: Int?
)
