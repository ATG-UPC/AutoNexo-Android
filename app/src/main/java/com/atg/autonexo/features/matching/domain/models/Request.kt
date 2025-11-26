package com.atg.autonexo.features.matching.domain.models

import java.time.LocalDateTime

data class Request (
    var id: Long,
    var userId: Long,
    var vehicleId: Long,
    var requestedServices: List<ServiceCatalog>,
    var description: String,
    var latitude: Double,
    var longitude: Double,
    var searchRadiusKm: Int.Companion,
    var status: RequestStatus,
    var cancelledAt: LocalDateTime?,
    var createdAt: LocalDateTime
)

