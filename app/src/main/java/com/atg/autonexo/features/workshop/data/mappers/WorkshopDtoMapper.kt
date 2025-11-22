package com.atg.autonexo.features.workshop.data.mappers

import com.atg.autonexo.features.workshop.data.remote.models.WorkshopResponseDto
import com.atg.autonexo.features.workshop.domain.models.Workshop
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun WorkshopResponseDto.toDomain(): Workshop {
    val deletedAt = this.deletedAt?.let { parseDateTime(it) }
    val createdAt = parseDateTime(this.createdAt) ?: LocalDateTime.now()
    val updatedAt = parseDateTime(this.updatedAt) ?: LocalDateTime.now()
    
    return Workshop(
        id = id,
        ownerUserId = ownerUserId,
        name = name,
        shortDescription = shortDescription,
        legalName = legalName,
        ruc = ruc,
        rucVerified = rucVerified,
        trustScore = trustScore,
        active = active,
        deletedAt = deletedAt,
        logoUrl = logoUrl,
        photoUrls = photoUrls ?: emptyList(),
        capabilityTags = capabilityTags ?: emptyList(),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

private fun parseDateTime(dateTimeString: String): LocalDateTime? {
    return try {
        LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME)
    } catch (e: Exception) {
        null
    }
}

