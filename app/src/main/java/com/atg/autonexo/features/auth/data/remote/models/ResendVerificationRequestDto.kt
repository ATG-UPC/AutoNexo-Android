package com.atg.autonexo.features.auth.data.remote.models

import com.google.gson.annotations.SerializedName

data class ResendVerificationRequestDto(
    @SerializedName("email")
    val email: String
)

