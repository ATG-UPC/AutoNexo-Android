package com.atg.autonexo.features.matchingbooking.domain.models

data class Booking(
    val id: String,
    val serviceRequestId: String,
    val offerId: String,
    val workshopId: String,
    val workshopName: String,
    val carOwnerId: String,
    val vehicleId: String,
    val scheduledDateTime: String,
    val status: String,
    val agreedPrice: Double,
    val finalPrice: Double?,
    val workPerformed: String?,
    val notes: String?,
    val createdAt: String,
    val completedAt: String?
)
