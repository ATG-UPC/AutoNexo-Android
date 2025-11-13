package com.atg.autonexo.features.matchingbooking.domain.repositories

import com.atg.autonexo.features.iam.domain.models.AuthResult
import com.atg.autonexo.features.matchingbooking.domain.models.Booking

interface BookingRepository {
    suspend fun getWorkshopBookings(status: String?, upcoming: Boolean?, page: Int?, size: Int?): AuthResult<List<Booking>>
    suspend fun getBookingById(bookingId: String): AuthResult<Booking>
    suspend fun confirmSchedule(bookingId: String): AuthResult<Booking>
    suspend fun completeService(bookingId: String, workPerformed: String?, notes: String?, finalPrice: Double?): AuthResult<Booking>
    suspend fun cancelBooking(bookingId: String): AuthResult<String>
}
