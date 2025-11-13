package com.atg.autonexo.features.matchingbooking.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * DTO para Offer - Coincide con OfferResource del backend
 */
data class OfferDto(
    @SerializedName("id") val id: Long?,
    @SerializedName("serviceRequestId") val serviceRequestId: Long?,
    @SerializedName("workshopId") val workshopId: Long?,
    @SerializedName("proposedPriceAmount") val proposedPriceAmount: Double?,
    @SerializedName("currency") val currency: String?,
    @SerializedName("proposedDate") val proposedDate: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("expiresAt") val expiresAt: String?,
    @SerializedName("acceptedAt") val acceptedAt: String?,
    @SerializedName("withdrawnAt") val withdrawnAt: String?
)
