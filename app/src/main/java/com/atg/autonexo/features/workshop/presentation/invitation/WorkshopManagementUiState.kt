package com.atg.autonexo.features.workshop.presentation.invitation

import com.atg.autonexo.features.workshop.domain.models.Invitation
import com.atg.autonexo.features.workshop.domain.models.WorkshopEmployee
import com.atg.autonexo.features.workshop.domain.models.WorkshopStaff

data class WorkshopManagementUiState(
    val workshopId: Long? = null,
    val invitationCode: String? = null,
    val employees: List<WorkshopStaff> = emptyList(),
    val activeEmployeesCount: Int = 0,
    val totalEmployeesCount: Int = 0,
    val isLoading: Boolean = false,
    val isLoadingEmployees: Boolean = false,
    val errorMessage: String? = null,

    // GETMYWORKSHOPLOCATION
    val street: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zip: String? = null,

    // GETMYWORKSHOP

    // Info
    val workshopName: String? = null,
    val workshopShortDescription: String? = null,
    val photoUrls: List<String> = emptyList(),
    val logoUrl: String? = null,
    val trustScore: Float? = null,

    // Tags
    val isLodingWorkshop: Boolean = false,
    val capabilityTags: List<String> = emptyList(),
)

