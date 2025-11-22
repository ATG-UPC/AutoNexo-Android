package com.atg.autonexo.features.workshop.data.repositories

import com.atg.autonexo.features.auth.data.remote.models.ErrorResponseDto
import com.atg.autonexo.features.workshop.data.mappers.toDomain
import com.atg.autonexo.features.workshop.data.remote.services.CatalogApiService
import com.atg.autonexo.features.workshop.domain.models.CapabilityTag
import com.atg.autonexo.features.workshop.domain.repositories.CatalogRepository
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CatalogRepositoryImpl @Inject constructor(
    private val apiService: CatalogApiService,
    private val gson: Gson
) : CatalogRepository {

    override suspend fun getCapabilityTags(): Result<List<CapabilityTag>> {
        return try {
            val response = apiService.getCapabilityTags()
            
            if (response.isSuccessful && response.body() != null) {
                val tags = response.body()!!.toDomain()
                Result.success(tags)
            } else {
                Result.failure(Exception(parseError(response, "Error al obtener tags")))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e, "Error al obtener tags")))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseError(response: retrofit2.Response<*>, defaultMessage: String): String {
        val errorBodyString = try {
            response.errorBody()?.string() ?: ""
        } catch (e: Exception) {
            ""
        }
        
        return when {
            errorBodyString.isNotBlank() -> {
                try {
                    val errorResponse = gson.fromJson(errorBodyString, ErrorResponseDto::class.java)
                    val backendMessage = errorResponse.message ?: errorResponse.error
                    
                    when (response.code()) {
                        400 -> backendMessage ?: "Datos inválidos"
                        401 -> "No autorizado"
                        403 -> "Acceso denegado"
                        404 -> "No encontrado"
                        500 -> backendMessage ?: "Error interno del servidor"
                        503 -> "Servicio no disponible"
                        else -> backendMessage ?: defaultMessage
                    }
                } catch (e: Exception) {
                    when (response.code()) {
                        400 -> "Datos inválidos"
                        401 -> "No autorizado"
                        403 -> "Acceso denegado"
                        404 -> "No encontrado"
                        500 -> "Error interno del servidor"
                        else -> defaultMessage
                    }
                }
            }
            else -> {
                when (response.code()) {
                    400 -> "Datos inválidos"
                    401 -> "No autorizado"
                    403 -> "Acceso denegado"
                    404 -> "No encontrado"
                    500 -> "Error interno del servidor"
                    else -> defaultMessage
                }
            }
        }
    }

    private fun parseHttpException(e: HttpException, defaultMessage: String): String {
        val errorBodyString = try {
            e.response()?.errorBody()?.string() ?: ""
        } catch (ex: Exception) {
            ""
        }
        
        return when {
            errorBodyString.isNotBlank() -> {
                try {
                    val errorResponse = gson.fromJson(errorBodyString, ErrorResponseDto::class.java)
                    val backendMessage = errorResponse.message ?: errorResponse.error
                    
                    when (e.code()) {
                        400 -> backendMessage ?: "Datos inválidos"
                        401 -> "No autorizado"
                        403 -> "Acceso denegado"
                        404 -> "No encontrado"
                        500 -> backendMessage ?: "Error interno del servidor"
                        else -> backendMessage ?: defaultMessage
                    }
                } catch (ex: Exception) {
                    when (e.code()) {
                        400 -> "Datos inválidos"
                        401 -> "No autorizado"
                        403 -> "Acceso denegado"
                        404 -> "No encontrado"
                        500 -> "Error interno del servidor"
                        else -> defaultMessage
                    }
                }
            }
            else -> {
                when (e.code()) {
                    400 -> "Datos inválidos"
                    401 -> "No autorizado"
                    403 -> "Acceso denegado"
                    404 -> "No encontrado"
                    500 -> "Error interno del servidor"
                    else -> defaultMessage
                }
            }
        }
    }
}

