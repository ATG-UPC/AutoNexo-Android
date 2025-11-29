package com.atg.autonexo.features.matching.domain.repositories

import com.atg.autonexo.features.matching.domain.models.Booking
import java.time.LocalDateTime

interface BookingRepository {
    suspend fun getMyBookings(): Result<List<Booking>>

    suspend fun acceptScheduleChange(bookingId: Long, newScheduledDate: LocalDateTime): Result<Unit>
}