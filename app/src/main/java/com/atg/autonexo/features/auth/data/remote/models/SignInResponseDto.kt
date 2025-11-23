package com.atg.autonexo.features.auth.data.remote.models

import com.google.gson.annotations.SerializedName

data class SignInResponseDto(
    @SerializedName("token")
    val token: String,
    @SerializedName("tokenType")
    val tokenType: String,
    @SerializedName("expiresIn")
    val expiresIn: Long,
    @SerializedName("user")
    val user: UserDto
)

