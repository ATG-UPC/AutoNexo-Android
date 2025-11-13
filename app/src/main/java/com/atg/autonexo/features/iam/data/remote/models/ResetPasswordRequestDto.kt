package com.atg.autonexo.features.iam.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * Request DTO para resetear contraseña con token
 * Endpoint: POST /api/v1/users/reset-password
 */
data class ResetPasswordRequestDto(
    @SerializedName("token")
    val token: String,
    
    @SerializedName("newPassword")
    val newPassword: String
)

