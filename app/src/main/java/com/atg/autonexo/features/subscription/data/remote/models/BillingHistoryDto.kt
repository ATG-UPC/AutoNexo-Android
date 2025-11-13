package com.atg.autonexo.features.subscription.data.remote.models

import com.google.gson.annotations.SerializedName

data class BillingHistoryDto(
    @SerializedName("payments")
    val payments: List<PaymentDto>?,
    
    @SerializedName("totalSpent")
    val totalSpent: Double?,
    
    @SerializedName("currentPlan")
    val currentPlan: String?
)

