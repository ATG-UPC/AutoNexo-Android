package com.atg.autonexo.features.matchingbooking.presentation.booking

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.features.iam.domain.models.AuthResult
import com.atg.autonexo.features.matchingbooking.domain.models.Booking
import com.atg.autonexo.features.matchingbooking.domain.repositories.BookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookingDetailUiState(
    val booking: Booking? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isProcessing: Boolean = false
)

@HiltViewModel
class BookingDetailViewModel @Inject constructor(
    private val bookingRepository: BookingRepository
) : ViewModel() {
    
    companion object {
        private const val TAG = "BookingDetailViewModel"
    }
    
    private val _uiState = MutableStateFlow(BookingDetailUiState())
    val uiState: StateFlow<BookingDetailUiState> = _uiState.asStateFlow()
    
    fun loadBooking(bookingId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                val result = bookingRepository.getBookingById(bookingId)
                
                when (result) {
                    is AuthResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                booking = result.data
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
                Log.e(TAG, "Exception loading booking", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error de conexión"
                    )
                }
            }
        }
    }
    
    fun confirmSchedule(bookingId: String, scheduledDate: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, errorMessage = null) }
            
            try {
                val result = bookingRepository.confirmSchedule(bookingId, scheduledDate)
                
                when (result) {
                    is AuthResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isProcessing = false,
                                booking = result.data
                            )
                        }
                    }
                    is AuthResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isProcessing = false,
                                errorMessage = result.message
                            )
                        }
                    }
                    else -> {
                        _uiState.update {
                            it.copy(
                                isProcessing = false,
                                errorMessage = "Error desconocido"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception confirming schedule", e)
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        errorMessage = e.message ?: "Error de conexión"
                    )
                }
            }
        }
    }
    
    fun proposeScheduleChange(bookingId: String, newScheduledDate: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, errorMessage = null) }
            
            try {
                val result = bookingRepository.proposeScheduleChange(bookingId, newScheduledDate)
                
                when (result) {
                    is AuthResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isProcessing = false,
                                booking = result.data
                            )
                        }
                    }
                    is AuthResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isProcessing = false,
                                errorMessage = result.message
                            )
                        }
                    }
                    else -> {
                        _uiState.update {
                            it.copy(
                                isProcessing = false,
                                errorMessage = "Error desconocido"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception proposing schedule change", e)
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        errorMessage = e.message ?: "Error de conexión"
                    )
                }
            }
        }
    }
    
    fun completeService(
        bookingId: String,
        mileage: Int,
        services: List<BookingRepository.ServicePerformed>,
        observations: String?,
        imageUrls: List<String>?,
        finalPriceAmount: Double?,
        currency: String?
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, errorMessage = null) }
            
            try {
                val result = bookingRepository.completeService(
                    bookingId,
                    mileage,
                    services,
                    observations,
                    imageUrls,
                    finalPriceAmount,
                    currency
                )
                
                when (result) {
                    is AuthResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isProcessing = false,
                                booking = result.data
                            )
                        }
                    }
                    is AuthResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isProcessing = false,
                                errorMessage = result.message
                            )
                        }
                    }
                    else -> {
                        _uiState.update {
                            it.copy(
                                isProcessing = false,
                                errorMessage = "Error desconocido"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception completing service", e)
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        errorMessage = e.message ?: "Error de conexión"
                    )
                }
            }
        }
    }
    
    fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, errorMessage = null) }
            
            try {
                val result = bookingRepository.cancelBooking(bookingId)
                
                when (result) {
                    is AuthResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isProcessing = false,
                                booking = null // Booking is cancelled
                            )
                        }
                    }
                    is AuthResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isProcessing = false,
                                errorMessage = result.message
                            )
                        }
                    }
                    else -> {
                        _uiState.update {
                            it.copy(
                                isProcessing = false,
                                errorMessage = "Error desconocido"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception cancelling booking", e)
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        errorMessage = e.message ?: "Error de conexión"
                    )
                }
            }
        }
    }
}
