package com.atg.autonexo.features.workshop.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * DTO para plantilla de servicio del taller
 */
data class ServiceTemplateDto(
    @SerializedName("id")
    val id: Long?,
    
    @SerializedName("serviceName")
    val serviceName: String?,
    
    @SerializedName("serviceCategory")
    val serviceCategory: String?,
    
    @SerializedName("basePrice")
    val basePrice: Double?,
    
    @SerializedName("estimatedDurationMinutes")
    val estimatedDurationMinutes: Int?,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("available")
    val available: Boolean?
)

