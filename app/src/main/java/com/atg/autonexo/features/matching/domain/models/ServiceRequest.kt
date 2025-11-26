package com.atg.autonexo.features.matching.domain.models

import java.time.LocalDateTime

data class ServiceRequest (
    var id: Long,
    var userId: Long,
    var vehicleId: Long,
    var requestedServices: List<ServiceCatalog>,
    var description: String,
    var latitude: Double,
    var longitude: Double,
    var searchRadiusKm: Int.Companion,
    var status: ServiceRequestStatus,
    var cancelledAt: LocalDateTime?,
    var createdAt: LocalDateTime
)

