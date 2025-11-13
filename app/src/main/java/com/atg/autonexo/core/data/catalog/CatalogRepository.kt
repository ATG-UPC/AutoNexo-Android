package com.atg.autonexo.core.data.catalog

import android.util.Log
import com.atg.autonexo.core.data.catalog.models.*
import com.atg.autonexo.features.iam.domain.models.AuthResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio para datos del catálogo
 * Estos son datos de referencia relativamente estáticos
 */
@Singleton
class CatalogRepository @Inject constructor(
    private val catalogService: CatalogService
) {
    
    companion object {
        private const val TAG = "CatalogRepository"
    }
    
    // ========== Brands ==========
    
    suspend fun getBrands(popularOnly: Boolean? = null): AuthResult<List<BrandDto>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting brands (popularOnly: $popularOnly)")
            val response = catalogService.getBrands(popularOnly)
            
            if (response.isSuccessful) {
                val brands = response.body() ?: emptyList()
                return@withContext AuthResult.Success(brands)
            } else {
                val errorMessage = "Error al obtener marcas"
                Log.e(TAG, "Get brands failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get brands exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
    
    suspend fun getModelsForBrand(brandId: Long): AuthResult<List<ModelDto>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting models for brand: $brandId")
            val response = catalogService.getModelsForBrand(brandId)
            
            if (response.isSuccessful) {
                val models = response.body() ?: emptyList()
                return@withContext AuthResult.Success(models)
            } else {
                val errorMessage = "Error al obtener modelos"
                Log.e(TAG, "Get models failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get models exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
    
    // ========== Services ==========
    
    suspend fun getServices(category: String? = null): AuthResult<List<ServiceCatalogDto>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting services (category: $category)")
            val response = catalogService.getServices(category)
            
            if (response.isSuccessful) {
                val services = response.body() ?: emptyList()
                return@withContext AuthResult.Success(services)
            } else {
                val errorMessage = "Error al obtener servicios"
                Log.e(TAG, "Get services failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get services exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
    
    suspend fun getServiceCategories(): AuthResult<List<String>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting service categories")
            val response = catalogService.getServiceCategories()
            
            if (response.isSuccessful) {
                val categories = response.body() ?: emptyList()
                return@withContext AuthResult.Success(categories)
            } else {
                val errorMessage = "Error al obtener categorías"
                Log.e(TAG, "Get categories failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get categories exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
    
    // ========== Capability Tags ==========
    
    suspend fun getCapabilityTags(category: String? = null): AuthResult<List<CapabilityTagDto>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting capability tags (category: $category)")
            val response = catalogService.getCapabilityTags(category)
            
            if (response.isSuccessful) {
                val tags = response.body() ?: emptyList()
                return@withContext AuthResult.Success(tags)
            } else {
                val errorMessage = "Error al obtener tags"
                Log.e(TAG, "Get tags failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get tags exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
    
    suspend fun getCapabilityTagCategories(): AuthResult<List<String>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting tag categories")
            val response = catalogService.getCapabilityTagCategories()
            
            if (response.isSuccessful) {
                val categories = response.body() ?: emptyList()
                return@withContext AuthResult.Success(categories)
            } else {
                val errorMessage = "Error al obtener categorías de tags"
                Log.e(TAG, "Get tag categories failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get tag categories exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
}

