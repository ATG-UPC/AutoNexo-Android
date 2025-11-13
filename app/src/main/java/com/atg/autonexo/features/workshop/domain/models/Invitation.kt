package com.atg.autonexo.features.workshop.domain.models

data class Invitation(
    val id: Long,
    val invitationCode: String,
    val workshopId: Long,
    val workshopName: String?,
    val invitedEmail: String,
    val status: String,
    val expiresAt: String?,
    val createdAt: String?
)

