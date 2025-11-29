package com.atg.autonexo.features.auth.data.remote.models

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequestDto(
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String
)

