package com.atg.autonexo.features.workshop.data.mappers

import com.atg.autonexo.features.workshop.data.remote.models.WorkshopStaffResponseDto
import com.atg.autonexo.features.workshop.domain.models.WorkshopStaff
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun WorkshopStaffResponseDto.toDomain(): WorkshopStaff {
    val createdAt = parseDateTime(this.createdAt) ?: LocalDateTime.now()

    return WorkshopStaff(
        id = id,
        userId = userId,
        // En el dominio es Long no-null, así que si viene null del backend
        // ponemos un valor por defecto. Ajusta esto según tu lógica.
        primaryLocationId = primaryLocationId ?: 0L,
        otherLocationIds = otherLocationIds,
        isActive = isActive,
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

