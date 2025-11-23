package com.atg.autonexo.features.auth.data.remote.models

import com.google.gson.annotations.SerializedName

data class ResetPasswordRequestDto(
    @SerializedName("token")
    val token: String,
    @SerializedName("newPassword")
    val newPassword: String
)

