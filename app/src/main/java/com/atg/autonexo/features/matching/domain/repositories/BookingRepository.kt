package com.atg.autonexo.features.matching.domain.repositories

import com.atg.autonexo.features.matching.domain.models.AcceptScheduleRequest
import com.atg.autonexo.features.matching.domain.models.Booking
import com.atg.autonexo.features.matching.domain.models.CompleteBookingRequest

interface BookingRepository {
    suspend fun getMyBookings(): Result<List<Booking>>
    suspend fun acceptScheduleChange(bookingId: Long, request: AcceptScheduleRequest): Result<Booking>
    suspend fun completeBooking(bookingId: Long, request: CompleteBookingRequest): Result<Booking>
    suspend fun cancelBooking(bookingId: Long): Result<Unit>
}