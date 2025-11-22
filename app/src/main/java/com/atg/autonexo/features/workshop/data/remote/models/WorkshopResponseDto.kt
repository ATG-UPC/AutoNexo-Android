package com.atg.autonexo.features.workshop.data.remote.models

import com.google.gson.annotations.SerializedName

data class WorkshopResponseDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("ownerUserId")
    val ownerUserId: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("shortDescription")
    val shortDescription: String?,
    @SerializedName("legalName")
    val legalName: String?,
    @SerializedName("ruc")
    val ruc: String?,
    @SerializedName("rucVerified")
    val rucVerified: Boolean,
    @SerializedName("trustScore")
    val trustScore: Float?,
    @SerializedName("active")
    val active: Boolean,
    @SerializedName("deletedAt")
    val deletedAt: String?,
    @SerializedName("logoUrl")
    val logoUrl: String?,
    @SerializedName("photoUrls")
    val photoUrls: List<String>,
    @SerializedName("capabilityTags")
    val capabilityTags: List<String>,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)

