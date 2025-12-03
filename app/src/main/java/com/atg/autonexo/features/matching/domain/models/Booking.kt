package com.atg.autonexo.features.matching.domain.models

import java.math.BigDecimal
import java.time.LocalDateTime

data class Booking (
    var id: Long,
    var offerId: Long,
    var userId: Long,
    var vehicleId: Long,
    var workshopId: Long,
    var scheduledDate: LocalDateTime,
    val proposedPriceAmount: BigDecimal,
    val finalPriceAmount: BigDecimal,
    val currency: String,
    val status: BookingStatus,
    var requestedServices: List<ServiceCatalog>,
    val description: String,
    val completedAt: LocalDateTime?,
    val pickedUpAt: LocalDateTime?,
    val cancelledAt: LocalDateTime?,
    val cancelledBy: Long?,
    val cancelledReason: String?,
    val createdAt: LocalDateTime
)