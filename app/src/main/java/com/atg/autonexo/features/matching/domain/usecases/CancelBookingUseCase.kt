package com.atg.autonexo.features.matching.domain.usecases

import com.atg.autonexo.features.matching.domain.repositories.BookingRepository
import javax.inject.Inject

class CancelBookingUseCase @Inject constructor(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(bookingId: Long) =
        repository.cancelBooking(bookingId)
}