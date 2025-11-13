package com.atg.autonexo.core.data.catalog

import com.atg.autonexo.core.data.catalog.models.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Service Retrofit para Catalog System (endpoints públicos)
 * Base path: /api/v1/catalog
 */
interface CatalogService {
    
    // ========== Brands ==========
    
    @GET("api/v1/catalog/brands")
    suspend fun getBrands(@Query("popularOnly") popularOnly: Boolean?): Response<List<BrandDto>>
    
    @GET("api/v1/catalog/brands/{brandId}/models")
    suspend fun getModelsForBrand(@Path("brandId") brandId: Long): Response<List<ModelDto>>
    
    // ========== Services ==========
    
    @GET("api/v1/catalog/services")
    suspend fun getServices(@Query("category") category: String?): Response<List<ServiceCatalogDto>>
    
    @GET("api/v1/catalog/services/categories")
    suspend fun getServiceCategories(): Response<List<String>>
    
    // ========== Capability Tags ==========
    
    @GET("api/v1/catalog/capability-tags")
    suspend fun getCapabilityTags(@Query("category") category: String?): Response<List<CapabilityTagDto>>
    
    @GET("api/v1/catalog/capability-tags/categories")
    suspend fun getCapabilityTagCategories(): Response<List<String>>
}

