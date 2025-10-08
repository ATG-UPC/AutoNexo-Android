package com.atg.autonexo.features.vehiclemaintenance.presentation.vehicle

import com.atg.autonexo.features.vehiclemaintenance.presentation.vehicle.models.VehicleUi

data class VehicleDetailUiState(
    val isLoading: Boolean = false,
    val vehicle: VehicleUi? = null,
    val brand: String = "",
    val model: String = "",
    val year: String = "",
    val licensePlate: String = "",
    val photoUrl: String? = null,
    val maintenanceLogUrl: String? = null,
    val brandError: String? = null,
    val modelError: String? = null,
    val yearError: String? = null,
    val licensePlateError: String? = null,
    val isSaveEnabled: Boolean = false,
    val error: String? = null
)

