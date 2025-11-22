package com.atg.autonexo.features.workshop.domain.usecases

import com.atg.autonexo.features.workshop.domain.repositories.WorkshopRepository
import javax.inject.Inject

class DeactivateEmployeeUseCase @Inject constructor(
    private val repository: WorkshopRepository
) {
    suspend operator fun invoke(workshopId: Long, employeeId: Long) = 
        repository.deactivateEmployee(workshopId, employeeId)
}

