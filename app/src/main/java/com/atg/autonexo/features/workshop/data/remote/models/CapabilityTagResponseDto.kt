package com.atg.autonexo.features.workshop.data.remote.models

import com.google.gson.annotations.SerializedName

data class CapabilityTagResponseDto(
    @SerializedName("code")
    val code: String,
    @SerializedName("displayName")
    val displayName: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("categoryDisplayName")
    val categoryDisplayName: String
)

