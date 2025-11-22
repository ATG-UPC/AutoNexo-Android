package com.atg.autonexo.features.workshop.domain.models

data class CreateInvitationRequest(
    val email: String? = null,
    val message: String? = null,
    val validityDays: Int? = 365
)

