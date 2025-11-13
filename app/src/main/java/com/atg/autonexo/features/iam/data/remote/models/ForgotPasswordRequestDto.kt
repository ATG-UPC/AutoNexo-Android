package com.atg.autonexo.features.iam.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * Request DTO para solicitar reset de contraseña
 * Endpoint: POST /api/v1/users/forgot-password
 */
data class ForgotPasswordRequestDto(
    @SerializedName("email")
    val email: String
)

