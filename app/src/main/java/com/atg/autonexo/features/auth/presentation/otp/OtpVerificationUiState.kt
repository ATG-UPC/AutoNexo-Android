package com.atg.autonexo.features.auth.presentation.otp

data class OtpVerificationUiState(
    val otp: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val canResend: Boolean = true,
    val resendCooldown: Int = 0
) {
    val isFormValid: Boolean
        get() = otp.length == 4 && otp.all { it.isDigit() }
}
