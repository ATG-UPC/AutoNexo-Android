package com.atg.autonexo.features.matchingbooking.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * Request DTO para crear una oferta
 * Endpoint: POST /api/offers
 * Coincide con CreateOfferResource del backend
 */
data class CreateOfferRequestDto(
    @SerializedName("serviceRequestId")
    val serviceRequestId: Long,
    
    @SerializedName("proposedPriceAmount")
    val proposedPriceAmount: Double,
    
    @SerializedName("currency")
    val currency: String,
    
    @SerializedName("proposedDate")
    val proposedDate: String?,
    
    @SerializedName("message")
    val message: String?
)

