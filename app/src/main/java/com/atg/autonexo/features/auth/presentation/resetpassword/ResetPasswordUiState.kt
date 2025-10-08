package com.atg.autonexo.features.auth.presentation.resetpassword

data class ResetPasswordUiState(
    val newPassword: String = "",
    val repeatPassword: String = "",
    val isNewPasswordVisible: Boolean = false,
    val isRepeatPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val newPasswordError: String? = null,
    val repeatPasswordError: String? = null,
    val isSuccess: Boolean = false
) {
    val isFormValid: Boolean
        get() = newPassword.isNotBlank() &&
                repeatPassword.isNotBlank() &&
                newPassword == repeatPassword &&
                newPassword.length >= 8 &&
                newPassword.any { it.isDigit() } &&
                newPassword.any { it.isLetter() } &&
                newPasswordError == null &&
                repeatPasswordError == null
}
