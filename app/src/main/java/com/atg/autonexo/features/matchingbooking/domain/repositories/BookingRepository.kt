package com.atg.autonexo.features.matchingbooking.domain.repositories

import com.atg.autonexo.features.iam.domain.models.AuthResult
import com.atg.autonexo.features.matchingbooking.domain.models.Booking

interface BookingRepository {
    suspend fun getWorkshopBookings(status: String?, upcoming: Boolean?, page: Int?, size: Int?): AuthResult<List<Booking>>
    suspend fun getBookingById(bookingId: String): AuthResult<Booking>
    suspend fun confirmSchedule(bookingId: String, scheduledDate: String): AuthResult<Booking>
    suspend fun proposeScheduleChange(bookingId: String, newScheduledDate: String): AuthResult<Booking>
    suspend fun completeService(
        bookingId: String,
        mileage: Int,
        services: List<ServicePerformed>,
        observations: String?,
        imageUrls: List<String>?,
        finalPriceAmount: Double?,
        currency: String?
    ): AuthResult<Booking>
    suspend fun cancelBooking(bookingId: String): AuthResult<String>
    
    data class ServicePerformed(
        val serviceType: String,
        val description: String?,
        val cost: Double
    )
}
