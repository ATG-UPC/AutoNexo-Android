package com.atg.autonexo.features.auth.data.remote.models

import com.google.gson.annotations.SerializedName

data class SignInRequestDto(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

