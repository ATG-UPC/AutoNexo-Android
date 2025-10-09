package com.atg.autonexo.features.iam.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * Response del backend para el endpoint POST /api/v1/users/signin
 * Estructura: AuthenticationResponseResource
 */
data class LoginResponseDto(
    @SerializedName("token")
    val token: String,
    
    @SerializedName("tokenType")
    val tokenType: String,
    
    @SerializedName("expiresIn")
    val expiresIn: Long,
    
    @SerializedName("user")
    val user: UserDto
)
