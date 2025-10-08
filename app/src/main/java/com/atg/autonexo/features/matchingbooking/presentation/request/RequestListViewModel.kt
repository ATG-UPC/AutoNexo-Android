package com.atg.autonexo.features.matchingbooking.presentation.request

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.features.matchingbooking.domain.model.ServiceRequestStatus
import com.atg.autonexo.features.matchingbooking.domain.model.ServiceType
import com.atg.autonexo.features.matchingbooking.presentation.request.models.ServiceRequestUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestListViewModel @Inject constructor(
    // TODO: Inyectar repositorio cuando esté listo
) : ViewModel() {

    private val _uiState = MutableStateFlow(RequestListUiState())
    val uiState: StateFlow<RequestListUiState> = _uiState.asStateFlow()

    init {
        loadRequests()
    }

    fun loadRequests() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Mock data
            val mockRequests = listOf(
                ServiceRequestUi(
                    serviceRequestId = "1",
                    ownerId = "owner1",
                    ownerName = "Sergio Iglesias",
                    ownerRating = 4.5,
                    vehicleId = "vehicle1",
                    vehicleDescription = "Nissan Sentra 2018",
                    serviceType = ServiceType.INSPECTION,
                    description = "I crashed my car 2 days ago, I need a change for both headlights.",
                    requestedDate = "22/10/25",
                    status = ServiceRequestStatus.OPEN,
                    timestamp = "21 hours",
                    offerCount = 2
                ),
                ServiceRequestUi(
                    serviceRequestId = "2",
                    ownerId = "owner2",
                    ownerName = "Sergio Iglesias",
                    ownerRating = 4.5,
                    vehicleId = "vehicle2",
                    vehicleDescription = "Toyota Corolla 2020",
                    serviceType = ServiceType.BRAKES,
                    description = "My car needs a change in both the air filter and the oil. I think it needs a starter check.",
                    requestedDate = "23/10/25",
                    status = ServiceRequestStatus.MATCHING,
                    timestamp = "5 hours",
                    offerCount = 1
                )
            )
            
            _uiState.update {
                it.copy(
                    isLoading = false,
                    requests = mockRequests
                )
            }
        }
    }

    fun filterRequests(filter: RequestFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
        // TODO: Aplicar filtro real cuando tengamos repositorio
    }
}

