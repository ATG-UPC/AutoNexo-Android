package com.atg.autonexo.features.workshop.data.remote.models

import com.google.gson.annotations.SerializedName

data class UploadResponseDto(
    @SerializedName("url")
    val url: String
)

