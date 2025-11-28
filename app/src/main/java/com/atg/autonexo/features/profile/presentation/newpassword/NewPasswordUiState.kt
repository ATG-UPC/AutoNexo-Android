package com.atg.autonexo.features.profile.presentation.newpassword

data class NewPasswordUiState(
    val newPassword: String = "",
    val repeatPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isPasswordChanged: Boolean = false
)

