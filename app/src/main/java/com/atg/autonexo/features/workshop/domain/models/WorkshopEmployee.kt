package com.atg.autonexo.features.workshop.domain.models

import java.time.LocalDateTime

data class WorkshopEmployee(
    val id: Long,
    val userId: Long,
    val workshopId: Long,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val phoneNumber: String?,
    val role: String, // WORKSHOP_EMPLOYEE
    val active: Boolean,
    val joinedAt: LocalDateTime
)

