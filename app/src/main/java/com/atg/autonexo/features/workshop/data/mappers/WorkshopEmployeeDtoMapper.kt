package com.atg.autonexo.features.workshop.data.mappers

import com.atg.autonexo.features.workshop.data.remote.models.WorkshopEmployeeResponseDto
import com.atg.autonexo.features.workshop.domain.models.WorkshopEmployee
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun WorkshopEmployeeResponseDto.toDomain(): WorkshopEmployee {
    return WorkshopEmployee(
        id = id,
        userId = userId,
        workshopId = workshopId,
        email = email,
        firstName = firstName,
        lastName = lastName,
        phoneNumber = phoneNumber,
        role = role,
        active = active,
        joinedAt = parseDateTime(joinedAt) ?: LocalDateTime.now()
    )
}

fun List<WorkshopEmployeeResponseDto>.toDomain(): List<WorkshopEmployee> {
    return this.map { it.toDomain() }
}

private fun parseDateTime(dateTimeString: String): LocalDateTime? {
    return try {
        LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME)
    } catch (e: Exception) {
        null
    }
}

