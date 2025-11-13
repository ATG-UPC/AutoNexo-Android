package com.atg.autonexo.features.iam.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * Response DTO para el estado de verificación de email
 * Endpoint: GET /api/v1/users/verification-status
 */
data class VerificationStatusResponseDto(
    @SerializedName("email")
    val email: String?,
    
    @SerializedName("isVerified")
    val isVerified: Boolean?
)

