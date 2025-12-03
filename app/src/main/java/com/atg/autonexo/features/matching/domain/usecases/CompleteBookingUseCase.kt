package com.atg.autonexo.features.matching.domain.usecases

import com.atg.autonexo.features.matching.domain.models.CompleteBookingRequest
import com.atg.autonexo.features.matching.domain.repositories.BookingRepository
import javax.inject.Inject

class CompleteBookingUseCase @Inject constructor(
    val repository: BookingRepository
) {
    suspend operator fun invoke(bookingId: Long, request: CompleteBookingRequest) =
        repository.completeBooking(bookingId, request)
}