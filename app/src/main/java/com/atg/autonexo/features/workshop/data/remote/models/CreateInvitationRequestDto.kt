package com.atg.autonexo.features.workshop.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * Request DTO para crear una invitación
 * Endpoint: POST /api/v1/invitations
 */
data class CreateInvitationRequestDto(
    @SerializedName("invitedEmail")
    val invitedEmail: String
)

