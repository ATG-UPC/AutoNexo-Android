package com.atg.autonexo.features.workshop.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * Request DTO para crear un taller
 * Endpoint: POST /api/v1/workshops
 * 
 * Campos según el backend:
 * - ownerUserId: Long (requerido)
 * - name: String (requerido, 3-200 caracteres)
 * - shortDescription: String? (opcional, max 500 caracteres)
 * - legalName: String? (opcional, max 300 caracteres)
 * - ruc: String? (opcional, exactamente 11 dígitos)
 */
data class CreateWorkshopRequestDto(
    @SerializedName("ownerUserId")
    val ownerUserId: Long,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("shortDescription")
    val shortDescription: String?,
    
    @SerializedName("legalName")
    val legalName: String?,
    
    @SerializedName("ruc")
    val ruc: String?
)

