package com.atg.autonexo.features.workshop.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * Request DTO para agregar una ubicación al taller
 * Endpoint: POST /api/v1/workshops/locations
 */
data class CreateLocationRequestDto(
    @SerializedName("address")
    val address: String,
    
    @SerializedName("district")
    val district: String,
    
    @SerializedName("city")
    val city: String,
    
    @SerializedName("latitude")
    val latitude: Double,
    
    @SerializedName("longitude")
    val longitude: Double,
    
    @SerializedName("isPrimary")
    val isPrimary: Boolean = false
)

