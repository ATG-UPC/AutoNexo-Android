package com.atg.autonexo.features.matchingbooking.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * Request DTO para completar un servicio
 * Endpoint: POST /api/service-bookings/{id}/complete
 */
data class CompleteServiceRequestDto(
    @SerializedName("workPerformed")
    val workPerformed: String?,
    
    @SerializedName("notes")
    val notes: String?,
    
    @SerializedName("finalPrice")
    val finalPrice: Double?
)

