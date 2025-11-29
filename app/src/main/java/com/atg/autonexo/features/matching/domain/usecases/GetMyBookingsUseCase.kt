package com.atg.autonexo.features.matching.domain.usecases

import com.atg.autonexo.features.matching.domain.models.Booking
import com.atg.autonexo.features.matching.domain.repositories.BookingRepository
import javax.inject.Inject

class GetMyBookingsUseCase @Inject constructor(
    private val repository: BookingRepository
) {
    suspend operator fun invoke() = repository.getMyBookings()

    suspend fun getBookings(): Result<List<Booking>>{
        return repository.getMyBookings().fold(
            onSuccess = { bookings ->
                Result.success(bookings)
            },
            onFailure = { exception ->
                Result.failure(exception)
            }
        )
    }
}