package com.atg.autonexo.features.workshop.data.remote.models

import com.google.gson.annotations.SerializedName

data class AcceptInvitationRequestDto(
    @SerializedName("invitationCode")
    val invitationCode: String,
    @SerializedName("email")
    val email: String
)

