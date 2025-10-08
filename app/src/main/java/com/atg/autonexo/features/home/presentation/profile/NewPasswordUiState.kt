package com.atg.autonexo.features.home.presentation.profile

data class NewPasswordUiState(
    val newPassword: String = "",
    val repeatPassword: String = "",
    val isNewPasswordVisible: Boolean = false,
    val isRepeatPasswordVisible: Boolean = false,
    val newPasswordError: String? = null,
    val repeatPasswordError: String? = null,
    val isLoading: Boolean = false,
    val isSaveEnabled: Boolean = false
)

