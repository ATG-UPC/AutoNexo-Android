package com.atg.autonexo.features.matchingbooking.presentation.dashboard.models

data class TimeSlotUi(
    val time: String,
    val isAvailable: Boolean = true,
    val hasAppointment: Boolean = false
)

