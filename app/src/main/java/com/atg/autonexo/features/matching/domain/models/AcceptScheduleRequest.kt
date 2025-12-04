package com.atg.autonexo.features.matching.domain.models

import java.time.LocalDateTime

data class AcceptScheduleRequest(
    val scheduledDate: LocalDateTime
)