package com.atg.autonexo.features.workshop.presentation.invitation

import com.atg.autonexo.features.workshop.domain.models.Invitation
import com.atg.autonexo.features.workshop.domain.models.WorkshopEmployee

data class WorkshopManagementUiState(
    val workshopId: Long? = null,
    val invitationCode: String? = null,
    val employees: List<WorkshopEmployee> = emptyList(),
    val activeEmployeesCount: Int = 0,
    val totalEmployeesCount: Int = 0,
    val isLoading: Boolean = false,
    val isLoadingEmployees: Boolean = false,
    val errorMessage: String? = null
)

