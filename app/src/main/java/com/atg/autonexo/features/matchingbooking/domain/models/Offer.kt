package com.atg.autonexo.features.matchingbooking.domain.models

data class Offer(
    val id: String,
    val serviceRequestId: String,
    val workshopId: String,
    val workshopName: String,
    val workshopRating: Double?,
    val estimatedPrice: Double,
    val estimatedDuration: Int, // en minutos
    val description: String,
    val status: String,
    val validUntil: String,
    val createdAt: String,
    val updatedAt: String?,
    val currency: String? = null,
    val proposedDate: String? = null
)
