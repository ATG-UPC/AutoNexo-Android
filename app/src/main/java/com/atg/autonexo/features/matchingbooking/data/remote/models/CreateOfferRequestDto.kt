package com.atg.autonexo.features.matchingbooking.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * Request DTO para crear una oferta
 * Endpoint: POST /api/offers
 */
data class CreateOfferRequestDto(
    @SerializedName("serviceRequestId")
    val serviceRequestId: String,
    
    @SerializedName("estimatedPrice")
    val estimatedPrice: Double,
    
    @SerializedName("estimatedDuration")
    val estimatedDuration: Int,
    
    @SerializedName("description")
    val description: String
)

