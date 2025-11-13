package com.atg.autonexo.features.workshop.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * DTO para el perfil completo del taller
 * Response de endpoints: GET /api/v1/workshops/my-workshop, GET /api/v1/workshops/{id}
 */
data class WorkshopDto(
    @SerializedName("id")
    val id: Long?,
    
    @SerializedName("name")
    val name: String?,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("contactEmail")
    val contactEmail: String?,
    
    @SerializedName("contactPhone")
    val contactPhone: String?,
    
    @SerializedName("logoUrl")
    val logoUrl: String?,
    
    @SerializedName("photoUrls")
    val photoUrls: List<String>?,
    
    @SerializedName("locations")
    val locations: List<LocationDto>?,
    
    @SerializedName("serviceTemplates")
    val serviceTemplates: List<ServiceTemplateDto>?,
    
    @SerializedName("capabilityTags")
    val capabilityTags: List<String>?,
    
    @SerializedName("subscriptionTier")
    val subscriptionTier: String?,
    
    @SerializedName("subscriptionStatus")
    val subscriptionStatus: String?,
    
    @SerializedName("trustScore")
    val trustScore: Double?,
    
    @SerializedName("ownerId")
    val ownerId: Long?,
    
    @SerializedName("active")
    val active: Boolean?,
    
    @SerializedName("createdAt")
    val createdAt: String?
)
