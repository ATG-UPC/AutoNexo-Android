package com.atg.autonexo.features.workshop.domain.usecases

import com.atg.autonexo.features.workshop.domain.models.Workshop
import com.atg.autonexo.features.workshop.domain.repositories.WorkshopRepository
import javax.inject.Inject

class UpdateWorkshopUseCase @Inject constructor(
    private val repository: WorkshopRepository
) {
    suspend operator fun invoke(workshop: Workshop) = repository.updateWorkshop(workshop)
}

