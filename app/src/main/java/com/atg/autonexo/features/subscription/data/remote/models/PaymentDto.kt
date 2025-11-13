package com.atg.autonexo.features.subscription.data.remote.models

import com.google.gson.annotations.SerializedName

data class PaymentDto(
    @SerializedName("id")
    val id: Long?,
    
    @SerializedName("workshopId")
    val workshopId: Long?,
    
    @SerializedName("amount")
    val amount: Double?,
    
    @SerializedName("currency")
    val currency: String?,
    
    @SerializedName("subscriptionTier")
    val subscriptionTier: String?,
    
    @SerializedName("status")
    val status: String?,
    
    @SerializedName("paymentMethod")
    val paymentMethod: String?,
    
    @SerializedName("createdAt")
    val createdAt: String?,
    
    @SerializedName("completedAt")
    val completedAt: String?
)

