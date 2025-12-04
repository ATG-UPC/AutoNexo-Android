package com.atg.autonexo.features.matching.presentation.booking

import com.atg.autonexo.features.matching.domain.models.Booking

data class BookingUiState(
    val isLoading: Boolean = false,
    val bookings: List<Booking> = emptyList(),
    val errorMessage: String? = null
)