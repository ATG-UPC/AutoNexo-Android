package com.atg.autonexo.features.matchingbooking.presentation.dashboard

import com.atg.autonexo.features.matchingbooking.presentation.dashboard.models.AppointmentUi

data class DashboardUiState(
    val isLoading: Boolean = false,
    val userName: String = "Arturo",
    val currentAppointment: AppointmentUi? = null,
    val selectedMonth: String = "October",
    val selectedYear: String = "2025",
    val error: String? = null
)

