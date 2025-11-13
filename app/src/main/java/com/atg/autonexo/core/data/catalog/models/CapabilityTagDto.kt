package com.atg.autonexo.core.data.catalog.models

import com.google.gson.annotations.SerializedName

data class CapabilityTagDto(
    @SerializedName("name")
    val name: String?,
    
    @SerializedName("category")
    val category: String?,
    
    @SerializedName("description")
    val description: String?
)

