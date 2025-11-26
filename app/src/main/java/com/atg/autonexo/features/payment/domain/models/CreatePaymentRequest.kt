package com.atg.autonexo.features.payment.domain.models

import java.math.BigDecimal

data class CreatePaymentRequest (
    val workshopId: Long,
    val subscriptionTier: SubscriptionTier,
    val paymentMethod: PaymentMethod,
    val paymentType: SubscriptionPaymentType,
    val description: String,
)