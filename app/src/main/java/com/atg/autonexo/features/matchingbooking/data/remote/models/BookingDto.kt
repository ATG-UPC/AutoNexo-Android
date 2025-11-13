package com.atg.autonexo.features.matchingbooking.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * DTO para Service Booking - Coincide con ServiceBookingResource del backend
 */
data class BookingDto(
    @SerializedName("id") val id: Long?,
    @SerializedName("serviceRequestId") val serviceRequestId: Long?,
    @SerializedName("offerId") val offerId: Long?,
    @SerializedName("userId") val userId: Long?,
    @SerializedName("vehicleId") val vehicleId: Long?,
    @SerializedName("workshopId") val workshopId: Long?,
    @SerializedName("scheduledDate") val scheduledDate: String?,
    @SerializedName("proposedPriceAmount") val proposedPriceAmount: Double?,
    @SerializedName("proposedPriceCurrency") val proposedPriceCurrency: String?,
    @SerializedName("finalPriceAmount") val finalPriceAmount: Double?,
    @SerializedName("finalPriceCurrency") val finalPriceCurrency: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("servicesToPerform") val servicesToPerform: List<String>?,
    @SerializedName("description") val description: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("completedAt") val completedAt: String?,
    @SerializedName("pickedUpAt") val pickedUpAt: String?,
    @SerializedName("cancelledAt") val cancelledAt: String?,
    @SerializedName("cancelledBy") val cancelledBy: Long?,
    @SerializedName("cancellationReason") val cancellationReason: String?
)
