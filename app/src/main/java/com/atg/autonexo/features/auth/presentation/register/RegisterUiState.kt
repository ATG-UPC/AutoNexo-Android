package com.atg.autonexo.features.auth.presentation.register

import com.atg.autonexo.features.auth.domain.models.Role

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val requestedRole: Role = Role.WORKSHOP_MANAGER,
    val invitationCode: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegisterSuccessful: Boolean = false
)

