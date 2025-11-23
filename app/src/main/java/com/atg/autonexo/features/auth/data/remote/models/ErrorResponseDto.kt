package com.atg.autonexo.features.auth.data.remote.models

import com.google.gson.annotations.SerializedName

data class ErrorResponseDto(
    @SerializedName("path")
    val path: String?,
    @SerializedName("error")
    val error: String?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("timestamp")
    val timestamp: String?,
    @SerializedName("status")
    val status: Int?
)

