package com.atg.autonexo.features.workshop.data.remote.models

import com.google.gson.annotations.SerializedName

data class UpdateWorkshopRequestDto(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("shortDescription")
    val shortDescription: String? = null,
    @SerializedName("legalName")
    val legalName: String? = null,
    @SerializedName("ruc")
    val ruc: String? = null
)

