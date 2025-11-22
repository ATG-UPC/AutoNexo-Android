package com.atg.autonexo.features.workshop.data.remote.models

import com.google.gson.annotations.SerializedName

data class CreateWorkshopRequestDto(
    @SerializedName("ownerUserId")
    val ownerUserId: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("shortDescription")
    val shortDescription: String? = null,
    @SerializedName("legalName")
    val legalName: String? = null,
    @SerializedName("ruc")
    val ruc: String? = null
)

