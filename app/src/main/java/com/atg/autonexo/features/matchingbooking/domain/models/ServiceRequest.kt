package com.atg.autonexo.features.matchingbooking.domain.models

/**
 * Domain model para Service Request - Coincide con el backend
 */
data class ServiceRequest(
    val id: Long,
    val userId: Long,
    val vehicleId: Long,
    val requestedServices: List<String>,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val searchRadiusKm: Int,
    val status: String,
    val createdAt: String?,
    val cancelledAt: String?
)
