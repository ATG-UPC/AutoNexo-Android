package com.atg.autonexo.features.matching.data.mappers

import com.atg.autonexo.features.matching.data.remote.models.OfferResponseDto
import com.atg.autonexo.features.matching.domain.models.Offer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun OfferResponseDto.toDomain(): Offer {
    val createdAt = parseDateTime(this.createdAt) ?: LocalDateTime.now()
    val withdrawAt = this.withdrawAt?.let { parseDateTime(it) }
    val proposedDate = parseDateTime(this.proposedDate)
    val expiresAt = this.expiresAt?.let { parseDateTime(it) }
    val acceptedAt = this.acceptedAt?.let { parseDateTime(it) }

    return Offer(
        id = id,
        serviceRequestId = serviceRequestId,
        workshopId = workshopId,
        proposedPriceAmount =  proposedPriceAmount,
        currency = currency,
        proposedDate = proposedDate,
        status = status,
        message = message,
        createdAt = createdAt,
        expiresAt = expiresAt,
        acceptedAt = acceptedAt,
        withdrawAt = withdrawAt,
    )
}

private fun parseDateTime(dateTimeString: String): LocalDateTime? {
    return try {
        LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME)
    } catch (e: Exception) {
        null
    }
}

fun List<OfferResponseDto>.toDomain(): List<Offer> {
    return this.map { it.toDomain() }
}