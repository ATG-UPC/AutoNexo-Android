package com.atg.autonexo.core.data.catalog.models

import com.google.gson.annotations.SerializedName

data class ModelDto(
    @SerializedName("id")
    val id: Long?,
    
    @SerializedName("name")
    val name: String?,
    
    @SerializedName("brandId")
    val brandId: Long?,
    
    @SerializedName("brandName")
    val brandName: String?,
    
    @SerializedName("active")
    val active: Boolean?
)

