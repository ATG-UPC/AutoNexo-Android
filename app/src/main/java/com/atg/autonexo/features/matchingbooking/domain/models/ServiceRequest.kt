package com.atg.autonexo.features.matchingbooking.domain.models

data class ServiceRequest(
    val id: Long,
    val carOwnerId: Long,
    val vehicleId: Long,
    val vehicleDescription: String,
    val serviceType: String,
    val description: String,
    val urgencyLevel: String,
    val preferredDate: String?,
    val latitude: Double,
    val longitude: Double,
    val status: String,
    val createdAt: String?,
    val offerCount: Int
)
