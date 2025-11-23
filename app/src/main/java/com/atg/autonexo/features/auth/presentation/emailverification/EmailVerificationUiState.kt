package com.atg.autonexo.features.auth.presentation.emailverification

data class EmailVerificationUiState(
    val token: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isVerificationSuccessful: Boolean = false,
    val message: String? = null
)

