package com.atg.autonexo.features.workshop.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * Request DTO para crear un taller
 * Endpoint: POST /api/v1/workshops
 */
data class CreateWorkshopRequestDto(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("contactEmail")
    val contactEmail: String,
    
    @SerializedName("contactPhone")
    val contactPhone: String,
    
    @SerializedName("address")
    val address: String,
    
    @SerializedName("district")
    val district: String,
    
    @SerializedName("city")
    val city: String,
    
    @SerializedName("latitude")
    val latitude: Double,
    
    @SerializedName("longitude")
    val longitude: Double
)

