package com.atg.autonexo.features.workshop.data.remote.models

import com.google.gson.annotations.SerializedName

data class CreateInvitationRequestDto(
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("validityDays")
    val validityDays: Int? = 365
)

