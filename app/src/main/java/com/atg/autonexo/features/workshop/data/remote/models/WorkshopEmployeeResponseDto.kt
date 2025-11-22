package com.atg.autonexo.features.workshop.data.remote.models

import com.google.gson.annotations.SerializedName

data class WorkshopEmployeeResponseDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("userId")
    val userId: Long,
    @SerializedName("workshopId")
    val workshopId: Long,
    @SerializedName("email")
    val email: String,
    @SerializedName("firstName")
    val firstName: String? = null,
    @SerializedName("lastName")
    val lastName: String? = null,
    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,
    @SerializedName("role")
    val role: String,
    @SerializedName("active")
    val active: Boolean,
    @SerializedName("joinedAt")
    val joinedAt: String
)

