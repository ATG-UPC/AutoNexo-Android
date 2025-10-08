package com.atg.autonexo.features.home.presentation.home.models

data class TimeSlotUi(
    val time: String,
    val isAvailable: Boolean = true,
    val hasAppointment: Boolean = false
)

