package com.atg.autonexo.features.matching.domain.models

import java.math.BigDecimal

data class ServicePerformed(
    val serviceType: String,
    val description: String,
    val cost: BigDecimal
)