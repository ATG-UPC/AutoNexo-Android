package com.atg.autonexo.features.matching.data.mappers

import com.atg.autonexo.features.matching.data.remote.models.ServiceRequestResponseDto
import com.atg.autonexo.features.matching.domain.models.ServiceRequest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun ServiceRequestResponseDto.toDomain(): ServiceRequest {
    val createdAt = parseDateTime(this.createdAt) ?: LocalDateTime.now()
    val cancelledAt = this.canceledAt?.let { parseDateTime(it) }

    return ServiceRequest(
        id = id,
        userId = userId,
        vehicleId = vehicleId,
        requestedServices = requestedServices,
        description = description,
        latitude = latitude,
        longitude = longitude,
        searchRadiusKm = Int,
        status = status,
        createdAt = createdAt,
        cancelledAt = cancelledAt
    )
}

private fun parseDateTime(dateTimeString: String): LocalDateTime? {
    return try {
        LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME)
    } catch (e: Exception) {
        null
    }
}

fun List<ServiceRequestResponseDto>.toDomain(): List<ServiceRequest> {
    return this.map { it.toDomain() }
}