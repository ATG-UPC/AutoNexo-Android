package com.atg.autonexo.features.matching.data.repositories

import com.atg.autonexo.features.auth.data.remote.models.ErrorResponseDto
import com.atg.autonexo.features.matching.data.mappers.toDomain
import com.atg.autonexo.features.matching.data.remote.models.CreateOfferRequestDto
import com.atg.autonexo.features.matching.data.remote.services.OfferApiService
import com.atg.autonexo.features.matching.domain.models.CreateOfferRequest
import com.atg.autonexo.features.matching.domain.models.Offer
import com.atg.autonexo.features.matching.domain.repositories.OfferRepository

import com.google.gson.Gson
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class OfferRepositoryImpl @Inject constructor(
    private val apiService: OfferApiService,
    private val gson: Gson
) : OfferRepository {

    override suspend fun getMyOffers(): Result<List<Offer>> {
        return try {
            val response = apiService.getMyOffers()

            if (response.isSuccessful && response.body() != null) {
                val offers = response.body()!!.toDomain()
                Result.success(offers)
            } else {
                Result.failure(Exception(parseError(response, "Error al obtener Ofertas")))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e, "Error al obtener Ofertas")))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createOffer(request: CreateOfferRequest): Result<Offer> {
        return try {
            val dto = CreateOfferRequestDto(
                serviceRequestId = request.serviceRequestId,
                proposedPriceAmount = request.proposedPriceAmount,
                currency = request.currency,
                proposedDate = request.proposedDate.toString(),
                message = request.message
            )
            val response = apiService.createOffer(dto)

            if (response.isSuccessful && response.body() != null) {
                val offer = response.body()!!.toDomain()
                Result.success(offer)
            } else {
                Result.failure(Exception(parseError(response, "Error al crear Oferta")))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e, "Error al obtener Oferta")))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun withdrawOffer(offerId: Long): Result<Unit> {
        return try {
            val response = apiService.withdrawOffer(offerId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(parseError(response, "Error al eliminar oferta")))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e, "Error al eliminar oferta")))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseError(response: Response<*>, defaultMessage: String): String {
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
                        409 -> backendMessage ?: "Conflicto"
                        422 -> backendMessage ?: "Datos de validación incorrectos"
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

