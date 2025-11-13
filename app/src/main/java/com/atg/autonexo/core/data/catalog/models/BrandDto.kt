package com.atg.autonexo.core.data.catalog.models

import com.google.gson.annotations.SerializedName

data class BrandDto(
    @SerializedName("id")
    val id: Long?,
    
    @SerializedName("name")
    val name: String?,
    
    @SerializedName("isPopular")
    val isPopular: Boolean?,
    
    @SerializedName("active")
    val active: Boolean?
)

