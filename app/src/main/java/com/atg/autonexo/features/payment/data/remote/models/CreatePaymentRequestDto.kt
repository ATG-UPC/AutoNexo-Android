package com.atg.autonexo.features.payment.data.remote.models

import com.atg.autonexo.features.payment.domain.models.PaymentMethod
import com.atg.autonexo.features.payment.domain.models.SubscriptionPaymentType
import com.atg.autonexo.features.payment.domain.models.SubscriptionTier
import com.google.gson.annotations.SerializedName

data class CreatePaymentRequestDto (
    @SerializedName("workshopId")
    val workshopId: Long,
    @SerializedName("subscriptionTier")
    val subscriptionTier: SubscriptionTier,
    @SerializedName("paymentMethod")
    val paymentMethod: PaymentMethod,
    @SerializedName("paymentType")
    val paymentType: SubscriptionPaymentType,
    @SerializedName("description")
    val description: String? = null,
)