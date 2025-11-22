package com.atg.autonexo.features.workshop.data.remote.services

import com.atg.autonexo.features.workshop.data.remote.models.CapabilityTagResponseDto
import retrofit2.Response
import retrofit2.http.GET

interface CatalogApiService {
    
    @GET("api/v1/catalog/capability-tags")
    suspend fun getCapabilityTags(): Response<List<CapabilityTagResponseDto>>
}

