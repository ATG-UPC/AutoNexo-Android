package com.atg.autonexo.features.matching.data.remote.models

import com.atg.autonexo.features.matching.domain.models.OfferStatus
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class OfferResponseDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("serviceRequestId")
    val serviceRequestId: Long,
    @SerializedName("workshopId")
    val workshopId: Long,
    @SerializedName("proposedPriceAmount")
    val proposedPriceAmount: BigDecimal,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("proposedDate")
    val proposedDate: String,
    @SerializedName("status")
    val status: OfferStatus,
    @SerializedName("message")
    val message: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("expiresAt")
    val expiresAt: String?,
    @SerializedName("acceptedAt")
    val acceptedAt: String?,
    @SerializedName("withdrawnAt")
    val withdrawAt: String?
)