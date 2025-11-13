package com.atg.autonexo.features.subscription.domain.models

data class Payment(
    val id: Long,
    val workshopId: Long,
    val amount: Double,
    val currency: String,
    val subscriptionTier: String,
    val status: String,
    val paymentMethod: String?,
    val createdAt: String?,
    val completedAt: String?
)

