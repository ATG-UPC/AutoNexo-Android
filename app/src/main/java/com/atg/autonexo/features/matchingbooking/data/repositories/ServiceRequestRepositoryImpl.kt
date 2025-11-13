package com.atg.autonexo.features.matchingbooking.data.repositories

import android.util.Log
import com.atg.autonexo.features.iam.domain.models.AuthResult
import com.atg.autonexo.features.matchingbooking.data.remote.models.ServiceRequestDto
import com.atg.autonexo.features.matchingbooking.data.remote.services.ServiceRequestService
import com.atg.autonexo.features.matchingbooking.domain.models.ServiceRequest
import com.atg.autonexo.features.matchingbooking.domain.repositories.ServiceRequestRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ServiceRequestRepositoryImpl @Inject constructor(
    private val serviceRequestService: ServiceRequestService
) : ServiceRequestRepository {
    
    companion object {
        private const val TAG = "ServiceRequestRepository"
    }
    
    override suspend fun getServiceRequestById(requestId: Long): AuthResult<ServiceRequest> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting service request by id: $requestId")
            val response = serviceRequestService.getServiceRequestById(requestId)
            
            if (response.isSuccessful) {
                val requestDto = response.body()
                if (requestDto != null) {
                    val serviceRequest = requestDto.toDomainModel()
                    return@withContext AuthResult.Success(serviceRequest)
                } else {
                    return@withContext AuthResult.Error("Empty response from server")
                }
            } else {
                val errorMessage = when (response.code()) {
                    404 -> "Solicitud no encontrada"
                    else -> "Error al obtener solicitud"
                }
                Log.e(TAG, "Get service request failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get service request exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
    
    override suspend fun getServiceRequests(status: String?, page: Int?, size: Int?): AuthResult<List<ServiceRequest>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Getting service requests")
            val response = serviceRequestService.getServiceRequests(status, page, size)
            
            if (response.isSuccessful) {
                val requestDtos = response.body() ?: emptyList()
                val serviceRequests = requestDtos.map { it.toDomainModel() }
                return@withContext AuthResult.Success(serviceRequests)
            } else {
                val errorMessage = "Error al obtener solicitudes"
                Log.e(TAG, "Get service requests failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get service requests exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
    
    override suspend fun rejectServiceRequest(requestId: Long): AuthResult<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Rejecting service request: $requestId")
            val response = serviceRequestService.rejectServiceRequest(requestId)
            
            if (response.isSuccessful) {
                val message = response.body() ?: "Solicitud rechazada"
                return@withContext AuthResult.Success(message)
            } else {
                val errorMessage = "Error al rechazar solicitud"
                Log.e(TAG, "Reject service request failed: ${response.code()}")
                return@withContext AuthResult.Error(errorMessage, response.code())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Reject service request exception", e)
            return@withContext AuthResult.Error(e.message ?: "Error de conexión", null)
        }
    }
    
    private fun ServiceRequestDto.toDomainModel(): ServiceRequest {
        return ServiceRequest(
            id = id ?: 0,
            carOwnerId = carOwnerId ?: 0,
            vehicleId = vehicleId ?: 0,
            vehicleDescription = vehicleDescription ?: "",
            serviceType = serviceType ?: "",
            description = description ?: "",
            urgencyLevel = urgencyLevel ?: "NORMAL",
            preferredDate = preferredDate,
            latitude = latitude ?: 0.0,
            longitude = longitude ?: 0.0,
            status = status ?: "PENDING",
            createdAt = createdAt,
            offerCount = offerCount ?: 0
        )
    }
}
