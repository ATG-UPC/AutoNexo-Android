package com.atg.autonexo.features.matching.data.remote.models

import com.atg.autonexo.features.matching.domain.models.BookingStatus
import com.atg.autonexo.features.matching.domain.models.ServiceCatalog
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class BookingResponseDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("offerId")
    val offerId: Long,
    @SerializedName("userId")
    val userId: Long,
    @SerializedName("vehicleId")
    val vehicleId: Long,
    @SerializedName("workshopId")
    val workshopId: Long,
    @SerializedName("scheduledDate")
    val scheduledDate: String?,
    @SerializedName("proposedPriceAmount")
    val proposedPriceAmount: BigDecimal,
    @SerializedName("finalPriceAmount")
    val finalPriceAmount: BigDecimal,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("status")
    val status: BookingStatus,
    @SerializedName("requestedServices")
    val requestedServices: List<ServiceCatalog>?,
    @SerializedName("description")
    val description: String,
    @SerializedName("completedAt")
    val completedAt: String?,
    @SerializedName("pickedUpAt")
    val pickedUpAt: String?,
    @SerializedName("cancelledAt")
    val cancelledAt: String?,
    @SerializedName("cancelledBy")
    val cancelledBy: Long?,
    @SerializedName("cancelledReason")
    val cancelledReason: String?,
    @SerializedName("createdAt")
    val createdAt: String
)