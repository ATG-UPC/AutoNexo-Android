package com.atg.autonexo.features.auth.data.remote.models

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequestDto(
    @SerializedName("currentPassword")
    val currentPassword: String,
    @SerializedName("newPassword")
    val newPassword: String
)

