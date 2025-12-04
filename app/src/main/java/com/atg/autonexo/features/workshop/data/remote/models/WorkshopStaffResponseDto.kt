package com.atg.autonexo.features.workshop.data.remote.models

import com.google.gson.annotations.SerializedName

data class WorkshopStaffResponseDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("userId")
    val userId: Long,
    @SerializedName("primaryLocationId")
    val primaryLocationId: Long?,
    @SerializedName("otherLocationIds")
    val otherLocationIds: List<Long>,
    @SerializedName("isActive")
    val isActive: Boolean,
    @SerializedName("createdAt")
    val createdAt: String
)
