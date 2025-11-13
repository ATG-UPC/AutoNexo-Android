package com.atg.autonexo.features.matchingbooking.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * Request DTO para completar un servicio
 * Endpoint: POST /api/service-bookings/{id}/complete
 * Coincide con MarkCompletedResource del backend
 */
data class CompleteServiceRequestDto(
    @SerializedName("mileage")
    val mileage: Int,
    
    @SerializedName("services")
    val services: List<ServicePerformedDto>,
    
    @SerializedName("observations")
    val observations: String?,
    
    @SerializedName("imageUrls")
    val imageUrls: List<String>?,
    
    @SerializedName("finalPriceAmount")
    val finalPriceAmount: Double?,
    
    @SerializedName("currency")
    val currency: String?
) {
    data class ServicePerformedDto(
        @SerializedName("serviceType")
        val serviceType: String,
        
        @SerializedName("description")
        val description: String?,
        
        @SerializedName("cost")
        val cost: Double
    )
}

