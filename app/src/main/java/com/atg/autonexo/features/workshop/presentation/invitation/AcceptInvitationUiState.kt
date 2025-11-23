package com.atg.autonexo.features.workshop.presentation.invitation

data class AcceptInvitationUiState(
    val code: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

