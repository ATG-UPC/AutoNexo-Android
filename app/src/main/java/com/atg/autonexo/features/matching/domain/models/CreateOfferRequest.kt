package com.atg.autonexo.features.matching.domain.models

import java.math.BigDecimal
import java.time.LocalDateTime

data class CreateOfferRequest(
    val serviceRequestId: Long,
    val proposedPriceAmount: BigDecimal,
    val currency: String,
    val proposedDate: LocalDateTime,
    val message: String
)