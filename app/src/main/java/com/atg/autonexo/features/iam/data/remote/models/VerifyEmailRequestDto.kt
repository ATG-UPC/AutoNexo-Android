package com.atg.autonexo.features.iam.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * Request DTO para verificar email con token
 * Endpoint: POST /api/v1/users/verify-email
 */
data class VerifyEmailRequestDto(
    @SerializedName("token")
    val token: String
)

