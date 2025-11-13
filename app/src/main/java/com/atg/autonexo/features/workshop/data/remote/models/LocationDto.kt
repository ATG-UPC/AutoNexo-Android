package com.atg.autonexo.features.workshop.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * DTO para ubicación del taller
 * Según LocationResource del backend
 */
data class LocationDto(
    @SerializedName("id")
    val id: Long?,
    
    @SerializedName("street")
    val street: String?,
    
    @SerializedName("city")
    val city: String?,
    
    @SerializedName("state")
    val state: String?,
    
    @SerializedName("zip")
    val zip: String?,
    
    @SerializedName("country")
    val country: String?,
    
    @SerializedName("latitude")
    val latitude: Double?,
    
    @SerializedName("longitude")
    val longitude: Double?,
    
    @SerializedName("active")
    val active: Boolean?,
    
    @SerializedName("createdAt")
    val createdAt: String?,
    
    @SerializedName("updatedAt")
    val updatedAt: String?
)

