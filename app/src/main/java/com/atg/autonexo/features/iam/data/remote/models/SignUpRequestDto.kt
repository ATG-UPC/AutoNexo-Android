package com.atg.autonexo.features.iam.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * Request para el endpoint POST /api/v1/users/signup
 * Estructura: SignUpResource del backend
 */
data class SignUpRequestDto(
    @SerializedName("email")
    val email: String,
    
    @SerializedName("password")
    val password: String,
    
    @SerializedName("firstName")
    val firstName: String,
    
    @SerializedName("lastName")
    val lastName: String,
    
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    
    @SerializedName("requestedRole")
    val requestedRole: String, // "CAR_OWNER", "WORKSHOP_MANAGER", "WORKSHOP_EMPLOYEE"
    
    @SerializedName("invitationCode")
    val invitationCode: String? = null // Solo requerido para WORKSHOP_EMPLOYEE
)




