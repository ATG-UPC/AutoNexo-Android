package com.atg.autonexo.features.workshop.domain.usecases

import com.atg.autonexo.features.workshop.domain.models.CreateWorkshopRequest
import com.atg.autonexo.features.workshop.domain.repositories.WorkshopRepository
import javax.inject.Inject

class CreateWorkshopUseCase @Inject constructor(
    private val repository: WorkshopRepository
) {
    suspend operator fun invoke(request: CreateWorkshopRequest) = repository.createWorkshop(request)
}

