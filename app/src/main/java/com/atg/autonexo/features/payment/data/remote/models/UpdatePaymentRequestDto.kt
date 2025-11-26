package com.atg.autonexo.features.payment.data.remote.models

import com.atg.autonexo.features.payment.domain.models.PaymentStatus
import com.atg.autonexo.features.payment.domain.models.SubscriptionStatus
import com.atg.autonexo.features.payment.domain.models.SubscriptionTier
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class UpdatePaymentRequestDto (
    @SerializedName("status")
    val status: SubscriptionStatus,
    @SerializedName("tier")
    val tier: SubscriptionTier,
    @SerializedName("expiresAt")
    val expiresAt: String
)