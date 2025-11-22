package com.atg.autonexo.features.workshop.domain.repositories

import com.atg.autonexo.features.workshop.domain.models.CapabilityTag

interface CatalogRepository {
    suspend fun getCapabilityTags(): Result<List<CapabilityTag>>
}

