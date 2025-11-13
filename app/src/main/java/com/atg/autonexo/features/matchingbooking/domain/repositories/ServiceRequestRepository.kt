package com.atg.autonexo.features.matchingbooking.domain.repositories

import com.atg.autonexo.features.iam.domain.models.AuthResult
import com.atg.autonexo.features.matchingbooking.domain.models.ServiceRequest

interface ServiceRequestRepository {
    suspend fun getServiceRequestById(requestId: Long): AuthResult<ServiceRequest>
    suspend fun getServiceRequests(status: String?, page: Int?, size: Int?): AuthResult<List<ServiceRequest>>
    suspend fun rejectServiceRequest(requestId: Long): AuthResult<String>
}
