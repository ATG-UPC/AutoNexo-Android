package com.atg.autonexo.features.matchingbooking.data.repositories

import android.util.Log
import com.atg.autonexo.features.iam.domain.models.AuthResult
import com.atg.autonexo.features.matchingbooking.domain.models.Booking
import com.atg.autonexo.features.matchingbooking.domain.repositories.BookingRepository
import com.atg.autonexo.features.matchingbooking.domain.repositories.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val bookingRepository: BookingRepository
) : HomeRepository {
    
    companion object {
        private const val TAG = "HomeRepository"
    }
    
    override suspend fun getUpcomingBookings(): AuthResult<List<Booking>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting upcoming bookings")
            // Get upcoming bookings (upcoming=true)
            return@withContext bookingRepository.getWorkshopBookings(
                status = null,
                upcoming = true,
                page = null,
                size = null
            )
        } catch (e: Exception) {
            Log.e(TAG, "Get upcoming bookings exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
}