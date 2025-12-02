package com.atg.autonexo.features.matching.data.remote.models

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class CompleteBookingRequestDto(
    @SerializedName("mileage")
    val mileage: Int,
    @SerializedName("services")
    val services: List<ServicePerformedDto>,
    @SerializedName("observations")
    val observations: String,
    @SerializedName("imageUrls")
    val imageUrls: List<String>,
    @SerializedName("finalPriceAmount")
    val finalPriceAmount: BigDecimal,
    @SerializedName("currency")
    val currency: String
)

data class ServicePerformedDto(
    @SerializedName("serviceType")
    val serviceType: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("cost")
    val cost: BigDecimal
)
