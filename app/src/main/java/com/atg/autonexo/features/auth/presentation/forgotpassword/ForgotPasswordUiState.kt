package com.atg.autonexo.features.auth.presentation.forgotpassword

data class ForgotPasswordUiState(
    val phone: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val phoneError: String? = null,
    val isSuccess: Boolean = false
) {
    val isFormValid: Boolean
        get() = phone.isNotBlank() && phoneError == null
}
