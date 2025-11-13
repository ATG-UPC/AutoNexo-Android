package com.atg.autonexo.features.matchingbooking.data.repositories

import android.util.Log
import com.atg.autonexo.features.iam.domain.models.AuthResult
import com.atg.autonexo.features.matchingbooking.data.remote.models.BookingDto
import com.atg.autonexo.features.matchingbooking.data.remote.models.CompleteServiceRequestDto
import com.atg.autonexo.features.matchingbooking.data.remote.models.ConfirmScheduleDto
import com.atg.autonexo.features.matchingbooking.data.remote.models.ProposeScheduleChangeDto
import com.atg.autonexo.features.matchingbooking.data.remote.services.BookingService
import com.atg.autonexo.features.matchingbooking.domain.models.Booking
import com.atg.autonexo.features.matchingbooking.domain.repositories.BookingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BookingRepositoryImpl @Inject constructor(
    private val bookingService: BookingService
) : BookingRepository {
    
    companion object {
        private const val TAG = "BookingRepository"
    }
    
    override suspend fun getWorkshopBookings(status: String?, upcoming: Boolean?, page: Int?, size: Int?): AuthResult<List<Booking>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting workshop bookings")
            val response = bookingService.getWorkshopBookings(status, upcoming, page, size)
            
            if (response.isSuccessful) {
                val bookingDtos = response.body() ?: emptyList()
                val bookings = bookingDtos.map { it.toDomainModel() }
                return@withContext AuthResult.Success(bookings)
            } else {
                val errorMessage = "Error al obtener reservas"
                Log.e(TAG, "Get bookings failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get bookings exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
    
    override suspend fun getBookingById(bookingId: String): AuthResult<Booking> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting booking by id: $bookingId")
            val bookingIdLong = bookingId.toLongOrNull()
                ?: return@withContext AuthResult.Error("Invalid booking ID format", null)
            
            val response = bookingService.getBookingById(bookingIdLong)
            
            if (response.isSuccessful) {
                val bookingDto = response.body()
                if (bookingDto != null) {
                    val booking = bookingDto.toDomainModel()
                    return@withContext AuthResult.Success(booking)
                } else {
                    return@withContext AuthResult.Error("Empty response from server")
                }
            } else {
                val errorMessage = when (response.code()) {
                    404 -> "Reserva no encontrada"
                    else -> "Error al obtener reserva"
                }
                Log.e(TAG, "Get booking failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get booking exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
    
    override suspend fun confirmSchedule(bookingId: String, scheduledDate: String): AuthResult<Booking> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Confirming schedule for booking: $bookingId")
            val bookingIdLong = bookingId.toLongOrNull()
                ?: return@withContext AuthResult.Error("Invalid booking ID format", null)
            
            val request = ConfirmScheduleDto(scheduledDate)
            val response = bookingService.confirmSchedule(bookingIdLong, request)
            
            if (response.isSuccessful) {
                val bookingDto = response.body()
                if (bookingDto != null) {
                    val booking = bookingDto.toDomainModel()
                    return@withContext AuthResult.Success(booking)
                } else {
                    return@withContext AuthResult.Error("Empty response from server")
                }
            } else {
                val errorMessage = "Error al confirmar horario"
                Log.e(TAG, "Confirm schedule failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Confirm schedule exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
    
    override suspend fun proposeScheduleChange(bookingId: String, newScheduledDate: String): AuthResult<Booking> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Proposing schedule change for booking: $bookingId")
            val bookingIdLong = bookingId.toLongOrNull()
                ?: return@withContext AuthResult.Error("Invalid booking ID format", null)
            
            val request = ProposeScheduleChangeDto(newScheduledDate)
            val response = bookingService.proposeScheduleChange(bookingIdLong, request)
            
            if (response.isSuccessful) {
                val bookingDto = response.body()
                if (bookingDto != null) {
                    val booking = bookingDto.toDomainModel()
                    return@withContext AuthResult.Success(booking)
                } else {
                    return@withContext AuthResult.Error("Empty response from server")
                }
            } else {
                val errorMessage = "Error al proponer cambio de horario"
                Log.e(TAG, "Propose schedule change failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Propose schedule change exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
    
    override suspend fun completeService(
        bookingId: String,
        mileage: Int,
        services: List<ServicePerformed>,
        observations: String?,
        imageUrls: List<String>?,
        finalPriceAmount: Double?,
        currency: String?
    ): AuthResult<Booking> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Completing service for booking: $bookingId")
            val bookingIdLong = bookingId.toLongOrNull()
                ?: return@withContext AuthResult.Error("Invalid booking ID format", null)
            
            val servicesDto = services.map { service ->
                CompleteServiceRequestDto.ServicePerformedDto(
                    serviceType = service.serviceType,
                    description = service.description,
                    cost = service.cost
                )
            }
            val request = CompleteServiceRequestDto(
                mileage = mileage,
                services = servicesDto,
                observations = observations,
                imageUrls = imageUrls,
                finalPriceAmount = finalPriceAmount,
                currency = currency
            )
            val response = bookingService.completeService(bookingIdLong, request)
            
            if (response.isSuccessful) {
                val bookingDto = response.body()
                if (bookingDto != null) {
                    val booking = bookingDto.toDomainModel()
                    return@withContext AuthResult.Success(booking)
                } else {
                    return@withContext AuthResult.Error("Empty response from server")
                }
            } else {
                val errorMessage = "Error al completar servicio"
                Log.e(TAG, "Complete service failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Complete service exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
    
    override suspend fun cancelBooking(bookingId: String): AuthResult<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Canceling booking: $bookingId")
            val bookingIdLong = bookingId.toLongOrNull()
                ?: return@withContext AuthResult.Error("Invalid booking ID format", null)
            
            val response = bookingService.cancelBooking(bookingIdLong)
            
            if (response.isSuccessful) {
                val message = response.body() ?: "Reserva cancelada"
                return@withContext AuthResult.Success(message)
            } else {
                val errorMessage = "Error al cancelar reserva"
                Log.e(TAG, "Cancel booking failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Cancel booking exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
    
    private fun BookingDto.toDomainModel(): Booking {
        return Booking(
            id = id?.toString() ?: "0",
            serviceRequestId = serviceRequestId?.toString() ?: "0",
            offerId = offerId?.toString() ?: "0",
            workshopId = workshopId?.toString() ?: "0",
            workshopName = "", // Backend doesn't provide workshop name in booking resource
            carOwnerId = userId?.toString() ?: "0",
            vehicleId = vehicleId?.toString() ?: "0",
            scheduledDateTime = scheduledDate ?: "",
            status = status ?: "PENDING",
            agreedPrice = proposedPriceAmount ?: 0.0,
            finalPrice = finalPriceAmount,
            workPerformed = description,
            notes = description, // Backend uses description field
            createdAt = createdAt ?: "",
            completedAt = completedAt
        )
    }
}
