package com.atg.autonexo.features.workshop.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * Request DTO para actualizar suscripción
 * Endpoint: PUT /api/v1/workshops/my-workshop/subscription
 */
data class UpdateSubscriptionRequestDto(
    @SerializedName("tier")
    val tier: String,
    
    @SerializedName("autoRenew")
    val autoRenew: Boolean?
)

