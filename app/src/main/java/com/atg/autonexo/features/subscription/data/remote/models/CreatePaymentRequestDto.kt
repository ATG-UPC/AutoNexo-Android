package com.atg.autonexo.features.subscription.data.remote.models

import com.google.gson.annotations.SerializedName

data class CreatePaymentRequestDto(
    @SerializedName("subscriptionTier")
    val subscriptionTier: String,
    
    @SerializedName("paymentMethod")
    val paymentMethod: String?
)

