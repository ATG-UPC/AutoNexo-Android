package com.atg.autonexo.features.auth.data.remote.models

import com.google.gson.annotations.SerializedName

data class SignUpRequestDto(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("requestedRole")
    val requestedRole: String,
    @SerializedName("invitationCode")
    val invitationCode: String?
)

