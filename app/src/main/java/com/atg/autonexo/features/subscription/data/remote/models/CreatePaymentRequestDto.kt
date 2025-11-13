package com.atg.autonexo.features.subscription.data.remote.models

import com.google.gson.annotations.SerializedName

data class CreatePaymentRequestDto(
    @SerializedName("workshopId")
    val workshopId: Long,
    
    @SerializedName("subscriptionTier")
    val subscriptionTier: String,
    
    @SerializedName("paymentMethod")
    val paymentMethod: String,
    
    @SerializedName("paymentType")
    val paymentType: String,
    
    @SerializedName("description")
    val description: String
)

