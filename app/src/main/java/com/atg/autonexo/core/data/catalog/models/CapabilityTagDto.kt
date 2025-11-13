package com.atg.autonexo.core.data.catalog.models

import com.google.gson.annotations.SerializedName

/**
 * DTO para capability tags del catálogo
 * Según la respuesta del backend: code, displayName, categoryDisplayName, description, category
 */
data class CapabilityTagDto(
    @SerializedName("code")
    val code: String?,
    
    @SerializedName("displayName")
    val displayName: String?,
    
    @SerializedName("category")
    val category: String?,
    
    @SerializedName("categoryDisplayName")
    val categoryDisplayName: String?,
    
    @SerializedName("description")
    val description: String?
)

