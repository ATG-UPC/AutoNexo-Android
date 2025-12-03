package com.atg.autonexo.features.matching.data.remote.models

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.time.LocalDateTime

data class CreateOfferRequestDto (
    @SerializedName("serviceRequestId")
    val serviceRequestId: Long,
    @SerializedName("proposedPriceAmount")
    val proposedPriceAmount: BigDecimal,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("proposedDate")
    val proposedDate: LocalDateTime,
    @SerializedName("message")
    val message: String
)