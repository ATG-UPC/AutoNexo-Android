package com.atg.autonexo.features.matchingbooking.presentation.request

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.features.iam.domain.models.AuthResult
import com.atg.autonexo.features.matchingbooking.domain.models.ServiceRequest
import com.atg.autonexo.features.matchingbooking.domain.repositories.OfferRepository
import com.atg.autonexo.features.matchingbooking.domain.repositories.ServiceRequestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RequestDetailUiState(
    val serviceRequest: ServiceRequest? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isCreatingOffer: Boolean = false,
    val offerCreated: Boolean = false
)

@HiltViewModel
class RequestDetailViewModel @Inject constructor(
    private val serviceRequestRepository: ServiceRequestRepository,
    private val offerRepository: OfferRepository
) : ViewModel() {
    
    companion object {
        private const val TAG = "RequestDetailViewModel"
    }
    
    private val _uiState = MutableStateFlow(RequestDetailUiState())
    val uiState: StateFlow<RequestDetailUiState> = _uiState.asStateFlow()
    
    fun loadServiceRequest(requestId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                val requestIdLong = requestId.toLongOrNull()
                    ?: run {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Invalid request ID"
                            )
                        }
                        return@launch
                    }
                
                val result = serviceRequestRepository.getServiceRequestById(requestIdLong)
                
                when (result) {
                    is AuthResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                serviceRequest = result.data
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
                Log.e(TAG, "Exception loading service request", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error de conexión"
                    )
                }
            }
        }
    }
    
    fun createOffer(serviceRequestId: String, price: Double, dateTime: String, message: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingOffer = true, errorMessage = null) }
            
            try {
                val result = offerRepository.createOffer(
                    serviceRequestId = serviceRequestId,
                    proposedPriceAmount = price,
                    currency = "PEN", // Default currency
                    proposedDate = dateTime,
                    message = message
                )
                
                when (result) {
                    is AuthResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isCreatingOffer = false,
                                offerCreated = true
                            )
                        }
                    }
                    is AuthResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isCreatingOffer = false,
                                errorMessage = result.message
                            )
                        }
                    }
                    else -> {
                        _uiState.update {
                            it.copy(
                                isCreatingOffer = false,
                                errorMessage = "Error desconocido"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception creating offer", e)
                _uiState.update {
                    it.copy(
                        isCreatingOffer = false,
                        errorMessage = e.message ?: "Error de conexión"
                    )
                }
            }
        }
    }
    
    fun resetOfferCreated() {
        _uiState.update { it.copy(offerCreated = false) }
    }
}

