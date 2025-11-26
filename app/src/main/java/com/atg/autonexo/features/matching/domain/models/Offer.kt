package com.atg.autonexo.features.matching.domain.models

import java.math.BigDecimal
import java.time.LocalDateTime

data class Offer(
    val id: Long,
    val serviceRequestId: Long,
    val workshopId: Long,
    val proposedPriceAmount: BigDecimal,
    val currency: String,
    val proposedDate: LocalDateTime?,
    val status: OfferStatus,
    val message: String,
    val createdAt: LocalDateTime,
    val expiresAt: LocalDateTime?,
    val acceptedAt: LocalDateTime?,
    val withdrawAt: LocalDateTime?
)