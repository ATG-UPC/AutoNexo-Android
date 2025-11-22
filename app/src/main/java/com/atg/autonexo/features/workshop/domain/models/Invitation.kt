package com.atg.autonexo.features.workshop.domain.models

import java.time.LocalDateTime

data class Invitation(
    val id: Long,
    val invitationCode: String,
    val email: String?,
    val workshopId: Long,
    val message: String?,
    val expiresAt: LocalDateTime,
    val used: Boolean,
    val expired: Boolean,
    val canBeUsed: Boolean,
    val createdAt: LocalDateTime
)

