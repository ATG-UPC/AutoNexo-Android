package com.atg.autonexo.features.payment.data.remote.models


import com.atg.autonexo.features.payment.domain.models.PaymentStatus
import com.atg.autonexo.features.payment.domain.models.SubscriptionTier
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class PaymentResponseDto (
    @SerializedName("status")
    val status: PaymentStatus?,              // "ACTIVE"
    @SerializedName("tier")
    val tier: SubscriptionTier?,             // "FREE"
    @SerializedName("expiresAt")
    val expiresAt: String?,           // 2025-11-26T03:46:44.568
    @SerializedName("isActive")
    val isActive: Boolean,                  // false
    @SerializedName("canAccessPremiumFeatures")
    val canAccessPremiumFeatures: Boolean   // false

)