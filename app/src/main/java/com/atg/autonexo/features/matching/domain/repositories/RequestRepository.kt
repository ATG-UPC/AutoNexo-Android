package com.atg.autonexo.features.matching.domain.repositories

import com.atg.autonexo.features.matching.domain.models.Request

interface RequestRepository {
    suspend fun getServiceRequests(): Result<List<Request>>
}