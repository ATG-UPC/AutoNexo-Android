package com.atg.autonexo.features.payment.data.repositories

import android.content.Context
import com.atg.autonexo.features.auth.data.remote.models.ErrorResponseDto
import com.atg.autonexo.features.payment.data.mappers.toDomain
import com.atg.autonexo.features.payment.data.mappers.toUpdateRequestDto
import com.atg.autonexo.features.payment.data.remote.models.CreatePaymentRequestDto
import com.atg.autonexo.features.payment.data.remote.models.UpdatePaymentRequestDto
import com.atg.autonexo.features.payment.data.remote.services.PaymentApiService
import com.atg.autonexo.features.payment.domain.models.CreatePaymentRequest
import com.atg.autonexo.features.payment.domain.models.Payment
import com.atg.autonexo.features.payment.domain.repositories.PaymentRepository

import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import retrofit2.Response


class PaymentRepositoryImpl @Inject constructor(
    private val apiService: PaymentApiService,
    private val gson: Gson,
    private val context: Context
) : PaymentRepository {

    override suspend fun createSubscription(request: CreatePaymentRequest): Result<Payment> {
        return try {
            val dto = CreatePaymentRequestDto(
                workshopId = request.workshopId,
                subscriptionTier = request.subscriptionTier,
                paymentMethod = request.paymentMethod,
                paymentType = request.paymentType,
                description = request.description,
            )

            val createResponse = apiService.createSubscription(dto)

            if (!createResponse.isSuccessful) {
                return Result.failure(
                    Exception(parseError(createResponse, "Error al crear suscripción"))
                )
            }

            val getResponse = apiService.getMySubscription()

            if (getResponse.isSuccessful && getResponse.body() != null) {
                Result.success(getResponse.body()!!.toDomain())
            } else {
                Result.failure(
                    Exception(parseError(getResponse, "La suscripción se creó pero no se pudo obtener el estado"))
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun updateSubscription(payment: Payment): Result<Payment> {
        return try {
            val dto = payment.toUpdateRequestDto()
            val response = apiService.updateSubscription(dto)

            if(response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.toDomain())
                } ?: Result.failure(Exception("La respuesta del servidor fue exitosa pero vacía."))
            } else {
                Result.failure(Exception(parseError(response, "Error al actualizar suscripción")))
            }
        }catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e, "Error al actualizar workshop")))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMySubscription(): Result<Payment> {
        return try {
            val response = apiService.getMySubscription()

            if (response.isSuccessful && response.body() != null) {
                response.body()?.let {
                    val payment = it.toDomain()
                    Result.success(payment)
                } ?: run {
                    Result.failure(Exception("La respuesta del servidor fue exitosa pero vacía."))
                }
            } else {
                Result.failure(Exception(parseError(response, "Error al obtener suscripción")))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e, "Error al obtener suscripción")))
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
