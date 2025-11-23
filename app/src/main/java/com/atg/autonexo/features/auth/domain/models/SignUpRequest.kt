package com.atg.autonexo.features.auth.domain.models

data class SignUpRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val requestedRole: Role,
    val invitationCode: String?
)

