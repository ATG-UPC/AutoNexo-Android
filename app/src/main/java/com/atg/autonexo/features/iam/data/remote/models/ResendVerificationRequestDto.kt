package com.atg.autonexo.features.iam.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * Request DTO para reenviar email de verificación
 * Endpoint: POST /api/v1/users/resend-verification
 */
data class ResendVerificationRequestDto(
    @SerializedName("email")
    val email: String
)

