package com.atg.autonexo.features.payment.domain.models

import java.time.LocalDateTime

data class Payment (
    /*val id: Long,
    val workshopId: Long,
    val subscriptionTier: SubscriptionTier?,
    val amount: BigDecimal?,
    val paymentMethod: PaymentMethod,
    val paymentType: SubscriptionPaymentType,
    val status: PaymentStatus?,
    val paymentDate: LocalDateTime?,
    val transactionId: String,
    val description: String,
    val billingPeriodStart: LocalDateTime,
    val billingPeriodEnd: LocalDateTime,
    val nextBillingDate: LocalDate,
    val invoiceUrl: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val deletedAt: LocalDateTime?,
     */
    val status: PaymentStatus,
    val subscriptionTier: SubscriptionTier,
    val expiresAt: LocalDateTime,
    val isActive: Boolean,
    val canAccessPremiumFeatures: Boolean
)