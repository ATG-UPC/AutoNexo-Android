package com.atg.autonexo.features.auth.presentation.resetpassword

data class ResetPasswordUiState(
    val token: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isPasswordResetSuccessful: Boolean = false
)

