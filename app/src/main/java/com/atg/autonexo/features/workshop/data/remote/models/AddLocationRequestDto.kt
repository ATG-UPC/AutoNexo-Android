package com.atg.autonexo.features.workshop.data.remote.models

import com.google.gson.annotations.SerializedName

data class AddLocationRequestDto(
    @SerializedName("street")
    val street: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("zip")
    val zip: String? = null,
    @SerializedName("country")
    val country: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double
)

