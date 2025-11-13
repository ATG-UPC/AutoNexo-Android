package com.atg.autonexo.features.workshop.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * Request DTO para agregar una ubicación al taller
 * Endpoint: POST /api/v1/workshops/locations
 * 
 * Campos según el backend:
 * - street: String (requerido)
 * - city: String (requerido)
 * - state: String (requerido)
 * - zip: String (requerido)
 * - country: String (requerido)
 * - latitude: Double? (opcional)
 * - longitude: Double? (opcional)
 */
data class CreateLocationRequestDto(
    @SerializedName("street")
    val street: String,
    
    @SerializedName("city")
    val city: String,
    
    @SerializedName("state")
    val state: String,
    
    @SerializedName("zip")
    val zip: String,
    
    @SerializedName("country")
    val country: String,
    
    @SerializedName("latitude")
    val latitude: Double?,
    
    @SerializedName("longitude")
    val longitude: Double?
)

