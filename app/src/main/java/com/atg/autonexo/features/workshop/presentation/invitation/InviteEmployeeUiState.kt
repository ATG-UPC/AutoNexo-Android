package com.atg.autonexo.features.workshop.presentation.invitation

import com.atg.autonexo.features.workshop.domain.models.Invitation

data class InviteEmployeeUiState(
    val email: String = "",
    val message: String = "",
    val validityDays: Int = 365,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val invitation: Invitation? = null
)

