package com.atg.autonexo.features.workshop.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * Request DTO para actualizar un taller
 * Endpoint: PUT /api/v1/workshops
 */
data class UpdateWorkshopRequestDto(
    @SerializedName("name")
    val name: String?,
    
    @SerializedName("shortDescription")
    val shortDescription: String?,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("contactEmail")
    val contactEmail: String?,
    
    @SerializedName("contactPhone")
    val contactPhone: String?
)

