package com.atg.autonexo.features.matchingbooking.data.remote.models

import com.google.gson.annotations.SerializedName

data class BookingDto(
    @SerializedName("id") val id: String,
    @SerializedName("serviceRequestId") val serviceRequestId: String,
    @SerializedName("offerId") val offerId: String,
    @SerializedName("workshopId") val workshopId: String,
    @SerializedName("workshopName") val workshopName: String? = null,
    @SerializedName("carOwnerId") val carOwnerId: String,
    @SerializedName("vehicleId") val vehicleId: String,
    @SerializedName("scheduledDateTime") val scheduledDateTime: String,
    @SerializedName("status") val status: String, // PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED
    @SerializedName("agreedPrice") val agreedPrice: Double,
    @SerializedName("finalPrice") val finalPrice: Double? = null,
    @SerializedName("workPerformed") val workPerformed: String? = null,
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String? = null,
    @SerializedName("completedAt") val completedAt: String? = null
)
