package com.atg.autonexo.features.matchingbooking.presentation.request.models

import com.atg.autonexo.features.matchingbooking.domain.model.ServiceRequestStatus
import com.atg.autonexo.features.matchingbooking.domain.model.ServiceType

data class ServiceRequestUi(
    val serviceRequestId: String,
    val ownerId: String,
    val ownerName: String,
    val ownerRating: Double,
    val vehicleId: String,
    val vehicleDescription: String, // ej. "Nissan Sentra 2018"
    val serviceType: ServiceType,
    val description: String,
    val requestedDate: String, // "22/10/25"
    val status: ServiceRequestStatus,
    val timestamp: String, // "21 hours"
    val offerCount: Int = 0
)

