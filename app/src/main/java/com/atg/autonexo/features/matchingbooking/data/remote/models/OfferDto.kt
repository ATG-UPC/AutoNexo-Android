package com.atg.autonexo.features.matchingbooking.data.remote.models

import com.google.gson.annotations.SerializedName

data class OfferDto(
    @SerializedName("id") val id: String,
    @SerializedName("serviceRequestId") val serviceRequestId: String,
    @SerializedName("workshopId") val workshopId: String,
    @SerializedName("workshopName") val workshopName: String? = null,
    @SerializedName("workshopRating") val workshopRating: Double? = null,
    @SerializedName("estimatedPrice") val estimatedPrice: Double,
    @SerializedName("estimatedDuration") val estimatedDuration: Int, // en minutos
    @SerializedName("description") val description: String,
    @SerializedName("status") val status: String, // PENDING, ACCEPTED, REJECTED, EXPIRED
    @SerializedName("validUntil") val validUntil: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String? = null
)
