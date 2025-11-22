package com.atg.autonexo.features.workshop.domain.usecases

import com.atg.autonexo.features.workshop.domain.repositories.WorkshopRepository
import javax.inject.Inject

class GetLocationByIdUseCase @Inject constructor(
    private val repository: WorkshopRepository
) {
    suspend operator fun invoke(workshopId: Long, locationId: Long) = 
        repository.getLocationById(workshopId, locationId)
}

