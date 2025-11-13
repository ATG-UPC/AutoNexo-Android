package com.atg.autonexo.features.workshop.domain.models

data class SubscriptionInfo(
    val tier: String,
    val status: String,
    val startDate: String?,
    val expiresAt: String?,
    val autoRenew: Boolean
)

