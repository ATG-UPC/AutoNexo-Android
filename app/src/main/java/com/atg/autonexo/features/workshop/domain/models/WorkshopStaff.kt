package com.atg.autonexo.features.workshop.domain.models

import java.time.LocalDateTime

data class WorkshopStaff (
    val id: Long,
    val userId: Long,
    val primaryLocationId: Long,
    val otherLocationIds: List<Long>,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
)
