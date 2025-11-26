package com.atg.autonexo.features.matching.domain.repositories

import com.atg.autonexo.features.matching.domain.models.ServiceRequest

interface ServiceRequestRepository {
    suspend fun getServiceRequests(): Result<List<ServiceRequest>>
}