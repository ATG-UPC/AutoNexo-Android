package com.atg.autonexo.features.workshop.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * Request DTO para agregar una plantilla de servicio
 * Endpoint: POST /api/v1/workshops/service-templates
 */
data class CreateServiceTemplateRequestDto(
    @SerializedName("serviceName")
    val serviceName: String,
    
    @SerializedName("serviceCategory")
    val serviceCategory: String,
    
    @SerializedName("basePrice")
    val basePrice: Double,
    
    @SerializedName("estimatedDurationMinutes")
    val estimatedDurationMinutes: Int,
    
    @SerializedName("description")
    val description: String?
)

