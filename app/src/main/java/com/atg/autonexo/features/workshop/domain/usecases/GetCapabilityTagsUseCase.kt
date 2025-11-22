package com.atg.autonexo.features.workshop.domain.usecases

import com.atg.autonexo.features.workshop.domain.repositories.CatalogRepository
import javax.inject.Inject

class GetCapabilityTagsUseCase @Inject constructor(
    private val repository: CatalogRepository
) {
    suspend operator fun invoke() = repository.getCapabilityTags()
}

