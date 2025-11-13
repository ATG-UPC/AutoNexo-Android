package com.atg.autonexo.features.iam.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * Request DTO para cambiar la contraseña
 * Endpoint: PUT /api/v1/users/me/password
 */
data class ChangePasswordRequestDto(
    @SerializedName("currentPassword")
    val currentPassword: String,
    
    @SerializedName("newPassword")
    val newPassword: String
)

