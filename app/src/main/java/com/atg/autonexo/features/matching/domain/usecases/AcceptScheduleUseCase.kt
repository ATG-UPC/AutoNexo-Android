package com.atg.autonexo.features.matching.domain.usecases

import com.atg.autonexo.features.matching.domain.models.AcceptScheduleRequest
import com.atg.autonexo.features.matching.domain.repositories.BookingRepository
import javax.inject.Inject

class AcceptScheduleUseCase @Inject constructor(
    private val repository: BookingRepository
) {
    suspend operator fun invoke(bookingId: Long, request: AcceptScheduleRequest) =
        repository.acceptScheduleChange(bookingId, request)
}