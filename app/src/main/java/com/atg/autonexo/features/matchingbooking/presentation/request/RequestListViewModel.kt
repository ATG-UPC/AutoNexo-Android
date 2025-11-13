package com.atg.autonexo.features.matchingbooking.presentation.request

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.features.iam.domain.models.AuthResult
import com.atg.autonexo.features.matchingbooking.domain.model.ServiceRequestStatus
import com.atg.autonexo.features.matchingbooking.domain.model.ServiceType
import com.atg.autonexo.features.matchingbooking.domain.repositories.OfferRepository
import com.atg.autonexo.features.matchingbooking.domain.repositories.ServiceRequestRepository
import com.atg.autonexo.features.matchingbooking.presentation.request.models.ServiceRequestUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class RequestListViewModel @Inject constructor(
    private val serviceRequestRepository: ServiceRequestRepository,
    private val offerRepository: com.atg.autonexo.features.matchingbooking.domain.repositories.OfferRepository
) : ViewModel() {

    companion object {
        private const val TAG = "RequestListViewModel"
    }

    private val _uiState = MutableStateFlow(RequestListUiState())
    val uiState: StateFlow<RequestListUiState> = _uiState.asStateFlow()

    init {
        loadRequests()
    }

    fun loadRequests() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                Log.d(TAG, "Cargando service requests desde el backend...")
                val result = serviceRequestRepository.getServiceRequests(
                    status = null, // Obtener todas las requests
                    page = null,
                    size = null
                )
                
                when (result) {
                    is AuthResult.Success -> {
                        val requests = result.data
                        Log.d(TAG, "Se obtuvieron ${requests.size} service requests")
                        
                        // Fetch offer counts for all requests
                        val offerCountsMap = mutableMapOf<String, Int>()
                        requests.forEach { request ->
                            val count = getOfferCount(request.id.toString())
                            offerCountsMap[request.id.toString()] = count
                        }
                        
                        // Mapear a UI model
                        val requestsUi = requests.map { request ->
                            ServiceRequestUi(
                                serviceRequestId = request.id.toString(),
                                ownerId = request.userId.toString(),
                                ownerName = "Usuario ${request.userId}", // TODO: Obtener nombre real del usuario - requiere endpoint de usuario
                                ownerRating = 4.0, // TODO: Obtener rating real - requiere endpoint de rating
                                vehicleId = request.vehicleId.toString(),
                                vehicleDescription = "Vehículo ${request.vehicleId}", // TODO: Obtener descripción real del vehículo - requiere endpoint de vehículo
                                serviceType = mapServicesToType(request.requestedServices),
                                description = request.description,
                                requestedDate = formatDate(request.createdAt),
                                status = mapStatus(request.status),
                                timestamp = calculateTimestamp(request.createdAt),
                                offerCount = offerCountsMap[request.id.toString()] ?: 0
                            )
                        }
                        
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                requests = requestsUi
                            )
                        }
                    }
                    is AuthResult.Error -> {
                        Log.e(TAG, "Error al cargar requests: ${result.message}")
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                    else -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Error desconocido"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción al cargar requests", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error de conexión"
                    )
                }
            }
        }
    }

    fun filterRequests(filter: RequestFilter) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedFilter = filter) }
            
            val status = when (filter) {
                RequestFilter.ALL -> null
                RequestFilter.PENDING -> "PENDING"
                RequestFilter.MATCHING -> "MATCHING"
                RequestFilter.ARCHIVED -> "ARCHIVED"
            }
            
            try {
                val result = serviceRequestRepository.getServiceRequests(
                    status = status,
                    page = null,
                    size = null
                )
                
                when (result) {
                    is AuthResult.Success -> {
                        val requests = result.data
                        
                        // Fetch offer counts for all requests
                        val offerCountsMap = mutableMapOf<String, Int>()
                        requests.forEach { request ->
                            val count = getOfferCount(request.id.toString())
                            offerCountsMap[request.id.toString()] = count
                        }
                        
                        val requestsUi = requests.map { request ->
                            ServiceRequestUi(
                                serviceRequestId = request.id.toString(),
                                ownerId = request.userId.toString(),
                                ownerName = "Usuario ${request.userId}", // TODO: Obtener nombre real del usuario - requiere endpoint de usuario
                                ownerRating = 4.0, // TODO: Obtener rating real - requiere endpoint de rating
                                vehicleId = request.vehicleId.toString(),
                                vehicleDescription = "Vehículo ${request.vehicleId}", // TODO: Obtener descripción real del vehículo - requiere endpoint de vehículo
                                serviceType = mapServicesToType(request.requestedServices),
                                description = request.description,
                                requestedDate = formatDate(request.createdAt),
                                status = mapStatus(request.status),
                                timestamp = calculateTimestamp(request.createdAt),
                                offerCount = offerCountsMap[request.id.toString()] ?: 0
                            )
                        }
                        
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                requests = requestsUi
                            )
                        }
                    }
                    is AuthResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                    else -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Error desconocido"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error de conexión"
                    )
                }
            }
        }
    }
    
    private fun mapServicesToType(services: List<String>): ServiceType {
        // Mapear el primer servicio a un tipo, o usar OTHER por defecto
        return when (services.firstOrNull()) {
            "OIL_CHANGE" -> ServiceType.OIL_CHANGE
            "TIRE_ROTATION", "TIRE_REPLACEMENT" -> ServiceType.TIRES
            "BRAKE_INSPECTION", "BRAKE_REPAIR" -> ServiceType.BRAKES
            "ENGINE_DIAGNOSTIC", "ENGINE_REPAIR" -> ServiceType.INSPECTION
            "BODY_REPAIR", "PAINT" -> ServiceType.BODYWORK
            else -> ServiceType.OTHER
        }
    }
    
    private fun mapStatus(status: String): ServiceRequestStatus {
        return when (status) {
            "PENDING" -> ServiceRequestStatus.OPEN
            "CANCELLED" -> ServiceRequestStatus.CANCELLED
            "OFFERED" -> ServiceRequestStatus.OFFERED
            "ARCHIVED" -> ServiceRequestStatus.ARCHIVED
            else -> ServiceRequestStatus.MATCHING
        }
    }
    
    private fun formatDate(dateString: String?): String {
        if (dateString == null) return "N/A"
        
        return try {
            val formatter = DateTimeFormatter.ISO_DATE_TIME
            val date = LocalDateTime.parse(dateString, formatter)
            val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yy")
            date.format(outputFormatter)
        } catch (e: Exception) {
            "N/A"
        }
    }
    
    private fun calculateTimestamp(dateString: String?): String {
        if (dateString == null) return "N/A"
        
        return try {
            val formatter = DateTimeFormatter.ISO_DATE_TIME
            val date = LocalDateTime.parse(dateString, formatter)
            val now = LocalDateTime.now()
            
            val hours = ChronoUnit.HOURS.between(date, now)
            val days = ChronoUnit.DAYS.between(date, now)
            
            when {
                hours < 1 -> "Hace menos de 1 hora"
                hours < 24 -> "Hace $hours horas"
                days == 1L -> "Hace 1 día"
                else -> "Hace $days días"
            }
        } catch (e: Exception) {
            "N/A"
        }
    }
    
    private suspend fun getOfferCount(serviceRequestId: String): Int {
        return try {
            val result = offerRepository.getOffersByServiceRequest(serviceRequestId)
            when (result) {
                is AuthResult.Success -> result.data.size
                else -> 0
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting offer count for request $serviceRequestId", e)
            0
        }
    }
}
