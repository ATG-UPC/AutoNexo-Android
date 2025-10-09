package com.atg.autonexo.features.iam.data.remote.models

/**
 * Response para el endpoint POST /api/v1/users/signup
 * El backend retorna un String simple: "User registered successfully"
 */
data class SignUpResponseDto(
    val message: String
)




