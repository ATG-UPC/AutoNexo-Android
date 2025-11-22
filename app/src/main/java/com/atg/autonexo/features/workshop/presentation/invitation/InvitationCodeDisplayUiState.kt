package com.atg.autonexo.features.workshop.presentation.invitation

data class InvitationCodeDisplayUiState(
    val invitationCode: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

