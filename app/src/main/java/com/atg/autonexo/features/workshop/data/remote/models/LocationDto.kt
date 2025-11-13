package com.atg.autonexo.features.workshop.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * DTO para ubicación del taller
 */
data class LocationDto(
    @SerializedName("id")
    val id: Long?,
    
    @SerializedName("address")
    val address: String?,
    
    @SerializedName("district")
    val district: String?,
    
    @SerializedName("city")
    val city: String?,
    
    @SerializedName("latitude")
    val latitude: Double?,
    
    @SerializedName("longitude")
    val longitude: Double?,
    
    @SerializedName("isPrimary")
    val isPrimary: Boolean?
)

