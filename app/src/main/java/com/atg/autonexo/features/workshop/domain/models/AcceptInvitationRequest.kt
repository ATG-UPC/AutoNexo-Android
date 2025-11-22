package com.atg.autonexo.features.workshop.domain.models

data class AcceptInvitationRequest(
    val invitationCode: String,
    val email: String
)

