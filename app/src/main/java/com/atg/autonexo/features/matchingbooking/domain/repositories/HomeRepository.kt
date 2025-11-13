package com.atg.autonexo.features.matchingbooking.domain.repositories

import com.atg.autonexo.features.iam.domain.models.AuthResult
import com.atg.autonexo.features.matchingbooking.domain.models.Booking

interface HomeRepository {
    suspend fun getUpcomingBookings(): AuthResult<List<Booking>>
}