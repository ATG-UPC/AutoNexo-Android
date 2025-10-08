package com.atg.autonexo.features.home.presentation.home

import com.atg.autonexo.features.home.presentation.home.models.AppointmentUi

data class HomeUiState(
    val isLoading: Boolean = false,
    val userName: String = "Arturo",
    val currentAppointment: AppointmentUi? = null,
    val selectedMonth: String = "October",
    val selectedYear: String = "2025",
    val error: String? = null
)

