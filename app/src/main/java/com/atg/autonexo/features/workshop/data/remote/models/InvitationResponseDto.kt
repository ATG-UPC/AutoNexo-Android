package com.atg.autonexo.features.workshop.data.remote.models

import com.google.gson.annotations.SerializedName

data class InvitationResponseDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("invitationCode")
    val invitationCode: String,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("workshopId")
    val workshopId: Long,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("expiresAt")
    val expiresAt: String,
    @SerializedName("used")
    val used: Boolean,
    @SerializedName("expired")
    val expired: Boolean,
    @SerializedName("canBeUsed")
    val canBeUsed: Boolean,
    @SerializedName("createdAt")
    val createdAt: String
)

