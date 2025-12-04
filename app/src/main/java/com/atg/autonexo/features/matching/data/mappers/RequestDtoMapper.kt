package com.atg.autonexo.features.matching.data.mappers

import com.atg.autonexo.features.matching.data.remote.models.RequestResponseDto
import com.atg.autonexo.features.matching.domain.models.Request
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun RequestResponseDto.toDomain(): Request {
    val createdAt = parseDateTime(this.createdAt) ?: LocalDateTime.now()

    return Request(
        id = id,
        vehicleId = vehicleId,
        requestedServices = requestedServices
            ?.filterNotNull()
            ?: emptyList(),
        description = description ?: "",
        matchScore = matchScore,
        latitude = userLocation.latitude,
        longitude = userLocation.longitude,
        distanceKm = distanceKm,
        status = status,
        createdAt = createdAt
    )
}

private fun parseDateTime(dateTimeString: String): LocalDateTime? {
    return try {
        LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME)
    } catch (e: Exception) {
        null
    }
}

fun List<RequestResponseDto>.toDomain(): List<Request> {
    return map { it.toDomain() }
}
