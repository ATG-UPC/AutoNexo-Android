package com.atg.autonexo.features.matching.domain.repositories

import com.atg.autonexo.features.matching.domain.models.Booking

interface BookingRepository {
    suspend fun getMyBookings(): Result<List<Booking>>
}