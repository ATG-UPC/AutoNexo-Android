package com.atg.autonexo.features.workshop.data.mappers

import com.atg.autonexo.features.workshop.data.remote.models.InvitationResponseDto
import com.atg.autonexo.features.workshop.domain.models.Invitation
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun InvitationResponseDto.toDomain(): Invitation {
    return Invitation(
        id = id,
        invitationCode = invitationCode,
        email = email,
        workshopId = workshopId,
        message = message,
        expiresAt = parseDateTime(expiresAt) ?: LocalDateTime.now().plusDays(365),
        used = used,
        expired = expired,
        canBeUsed = canBeUsed,
        createdAt = parseDateTime(createdAt) ?: LocalDateTime.now()
    )
}

fun List<InvitationResponseDto>.toDomain(): List<Invitation> {
    return this.map { it.toDomain() }
}

private fun parseDateTime(dateTimeString: String): LocalDateTime? {
    return try {
        LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME)
    } catch (e: Exception) {
        null
    }
}

