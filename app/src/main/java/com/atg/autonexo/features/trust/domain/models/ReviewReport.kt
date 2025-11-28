package com.atg.autonexo.features.trust.domain.models

import java.time.LocalDateTime

data class ReviewReport (
    val id: Long,
    val reportId: Long,
    val createdAt: LocalDateTime,
)