package com.atg.autonexo.features.matching.domain.models

import java.time.LocalDateTime

data class Request(
    val id: Long,
    val vehicleId: Long,
    val requestedServices: List<String>,
    val description: String,
    val matchScore: Double,
    val latitude: Double,
    val longitude: Double,
    val distanceKm: Double,
    val status: RequestStatus,
    val createdAt: LocalDateTime
)
