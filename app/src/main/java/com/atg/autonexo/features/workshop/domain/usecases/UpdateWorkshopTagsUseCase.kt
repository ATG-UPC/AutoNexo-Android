package com.atg.autonexo.features.workshop.domain.usecases

import com.atg.autonexo.features.workshop.domain.repositories.WorkshopRepository
import javax.inject.Inject

class UpdateWorkshopTagsUseCase @Inject constructor(
    private val repository: WorkshopRepository
) {
    suspend operator fun invoke(workshopId: Long, tags: List<String>) = 
        repository.updateTags(workshopId, tags)
}

