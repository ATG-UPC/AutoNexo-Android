package com.atg.autonexo.features.matching.data.repositories

import android.content.Context
import android.net.Uri
import com.atg.autonexo.features.auth.data.remote.models.ErrorResponseDto
import com.atg.autonexo.features.matching.data.mappers.toDomain
import com.atg.autonexo.features.matching.data.remote.models.AcceptScheduleRequestDto
import com.atg.autonexo.features.matching.data.remote.models.CompleteBookingRequestDto
import com.atg.autonexo.features.matching.data.remote.models.ServicePerformedDto
import com.atg.autonexo.features.matching.data.remote.services.BookingApiService
import com.atg.autonexo.features.matching.domain.models.AcceptScheduleRequest
import com.atg.autonexo.features.matching.domain.models.Booking
import com.atg.autonexo.features.matching.domain.models.CompleteBookingRequest
import com.atg.autonexo.features.matching.domain.repositories.BookingRepository
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDateTime
import javax.inject.Inject

class BookingRepositoryImpl @Inject constructor(
    private val apiService: BookingApiService,
    private val gson: Gson
) : BookingRepository {

    override suspend fun getMyBookings(): Result<List<Booking>>{
        return try {
            val response = apiService.getMyBookings()

            if (response.isSuccessful && response.body() != null) {
                val bookings = response.body()!!.toDomain()
                Result.success(bookings)
        } else {
                Result.failure(Exception(parseError(response, "Error al obtener Agenda")))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e, "Error al obtener Agenda")))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun acceptScheduleChange(
        bookingId: Long,
        request: AcceptScheduleRequest
    ): Result<Booking> {
        return try {
            val dto = AcceptScheduleRequestDto(
                newScheduledDate = request.newScheduledDate
            )

            val response = apiService.acceptScheduleChange(bookingId,dto)

            if (response.isSuccessful && response.body() != null) {
                val booking = response.body()!!.toDomain()
                Result.success(booking)
            } else {
                Result.failure(Exception(parseError(response, "Error al aceptar cambio de horario")))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e, "Error al obtener invitación")))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun completeBooking(
        bookingId: Long,
        request: CompleteBookingRequest
    ): Result<Booking> {
        return try {

            val dto = CompleteBookingRequestDto(
                mileage = request.mileage,
                services = request.services.map {
                    ServicePerformedDto(
                        serviceType = it.serviceType,
                        description = it.description,
                        cost = it.cost
                    )
                },
                observations = request.observations,
                imageUrls = request.imageUrls,
                finalPriceAmount = request.finalPriceAmount,
                currency = request.currency
            )

            val response = apiService.completeBooking(bookingId, dto)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception(parseError(response, "Error al completar servicio")))
            }

        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e, "Error al completar servicio")))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelBooking(bookingId: Long): Result<Unit> {
        return try {
            val response = apiService.cancelBooking(bookingId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(parseError(response, "Error al cancelar servicio")))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpException(e, "Error al cancelar servicio")))
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