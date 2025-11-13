package com.atg.autonexo.features.workshop.domain.models

data class ServiceTemplate(
    val id: Long,
    val serviceName: String,
    val serviceCategory: String,
    val basePrice: Double,
    val estimatedDurationMinutes: Int,
    val description: String?,
    val available: Boolean
)

