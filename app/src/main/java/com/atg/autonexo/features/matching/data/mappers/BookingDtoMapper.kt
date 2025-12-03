package com.atg.autonexo.features.matching.data.mappers

import com.atg.autonexo.features.matching.data.remote.models.BookingResponseDto
import com.atg.autonexo.features.matching.domain.models.Booking
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun BookingResponseDto.toDomain(): Booking {
    val scheduledDate = this.scheduledDate?.let { parseDateTime(it) }
    val completedAt = this.completedAt?.let { parseDateTime(it) }
    val pickedUpAt = this.pickedUpAt?.let { parseDateTime(it) }
    val cancelledAt = this.cancelledAt?.let { parseDateTime(it) }
    val createdAt = parseDateTime(this.createdAt) ?: LocalDateTime.now()

    return Booking(
        id = id,
        offerId = offerId,
        userId = userId,
        vehicleId = vehicleId,
        workshopId = workshopId,
        scheduledDate = scheduledDate ?: LocalDateTime.now(),
        proposedPriceAmount = proposedPriceAmount,
        finalPriceAmount = finalPriceAmount,
        currency = currency,
        status = status,
        requestedServices = requestedServices,
        description = description,
        completedAt = completedAt,
        pickedUpAt = pickedUpAt,
        cancelledAt = cancelledAt,
        cancelledBy = cancelledBy,
        cancelledReason = cancelledReason,
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

fun List<BookingResponseDto>.toDomain(): List<Booking> {
    return this.map { it.toDomain() }
}