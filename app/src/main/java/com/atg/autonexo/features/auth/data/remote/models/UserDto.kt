package com.atg.autonexo.features.auth.data.remote.models

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("email")
    val email: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("isVerified")
    val isVerified: Boolean,
    @SerializedName("active")
    val active: Boolean,
    @SerializedName("roles")
    val roles: List<String>,
    @SerializedName("workshopId")
    val workshopId: Long?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?
)

