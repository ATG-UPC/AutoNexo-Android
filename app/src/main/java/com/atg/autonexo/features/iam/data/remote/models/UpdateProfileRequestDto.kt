package com.atg.autonexo.features.iam.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * Request DTO para actualizar el perfil del usuario
 * Endpoint: PUT /api/v1/users/me
 */
data class UpdateProfileRequestDto(
    @SerializedName("firstName")
    val firstName: String,
    
    @SerializedName("lastName")
    val lastName: String,
    
    @SerializedName("phoneNumber")
    val phoneNumber: String
)

