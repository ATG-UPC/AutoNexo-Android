package com.atg.autonexo.features.vehiclemaintenance.presentation.vehicle.models

data class VehicleUi(
    val vehicleId: String,
    val licensePlate: String,
    val brand: String,
    val model: String,
    val year: Int,
    val ownerId: String,
    val photoUrl: String? = null,
    val maintenanceLogUrl: String? = null
)

