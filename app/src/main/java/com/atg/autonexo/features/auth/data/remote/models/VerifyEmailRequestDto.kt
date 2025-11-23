package com.atg.autonexo.features.auth.data.remote.models

import com.google.gson.annotations.SerializedName

data class VerifyEmailRequestDto(
    @SerializedName("token")
    val token: String
)

