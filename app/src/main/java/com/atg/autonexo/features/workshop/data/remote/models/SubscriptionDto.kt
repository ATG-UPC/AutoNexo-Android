package com.atg.autonexo.features.workshop.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * DTO para el estado de suscripción del taller
 * Response de: GET /api/v1/workshops/my-workshop/subscription
 */
data class SubscriptionDto(
    @SerializedName("tier")
    val tier: String?,
    
    @SerializedName("status")
    val status: String?,
    
    @SerializedName("startDate")
    val startDate: String?,
    
    @SerializedName("expiresAt")
    val expiresAt: String?,
    
    @SerializedName("autoRenew")
    val autoRenew: Boolean?
)

