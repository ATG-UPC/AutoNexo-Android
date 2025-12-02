package com.atg.autonexo.features.matching.domain.models

import java.math.BigDecimal

data class CompleteBookingRequest(
    var mileage: Int,
    var services: List<ServicePerformed>,
    var observations: String,
    var imageUrls: List<String>,
    var finalPriceAmount: BigDecimal,
    var currency: String
)