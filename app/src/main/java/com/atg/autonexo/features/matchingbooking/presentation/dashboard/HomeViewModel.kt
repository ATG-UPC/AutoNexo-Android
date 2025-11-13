package com.atg.autonexo.features.matchingbooking.presentation.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.core.data.UserPreferences
import com.atg.autonexo.features.iam.domain.models.AuthResult
import com.atg.autonexo.features.matchingbooking.domain.repositories.BookingRepository
import com.atg.autonexo.features.matchingbooking.domain.repositories.HomeRepository
import com.atg.autonexo.features.matchingbooking.presentation.dashboard.models.AppointmentUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class HomeUiState(
    val userName: String = "Usuario",
    val selectedMonth: String = "October",
    val selectedYear: String = "2025",
    val currentAppointment: AppointmentUi? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val homeRepository: HomeRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    companion object {
        private const val TAG = "DashboardViewModel"
    }

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadUpcomingBookings()
    }

    private fun loadUpcomingBookings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                Log.d(TAG, "Loading upcoming bookings...")
                val result = homeRepository.getUpcomingBookings()
                
                when (result) {
                    is AuthResult.Success -> {
                        val bookings = result.data
                        Log.d(TAG, "Loaded ${bookings.size} upcoming bookings")
                        
                        // Get the first upcoming booking as current appointment
                        val currentBooking = bookings.firstOrNull()
                        val appointmentUi = currentBooking?.let { booking ->
                            AppointmentUi(
                                id = booking.id,
                                date = formatDate(booking.scheduledDateTime),
                                time = formatTime(booking.scheduledDateTime),
                                owner = "Usuario ${booking.carOwnerId}", // TODO: Get real owner name
                                mechanic = booking.workshopName.ifEmpty { "Taller ${booking.workshopId}" }
                            )
                        }
                        
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                currentAppointment = appointmentUi
                            )
                        }
                    }
                    is AuthResult.Error -> {
                        Log.e(TAG, "Error loading bookings: ${result.message}")
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
                Log.e(TAG, "Exception loading bookings", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error de conexión"
                    )
                }
            }
        }
    }

    fun onPostponeAppointment() {
        val appointment = _uiState.value.currentAppointment ?: return
        viewModelScope.launch {
            // TODO: Implement postpone logic - may need to propose schedule change
            Log.d(TAG, "Postpone appointment: ${appointment.id}")
        }
    }

    fun onCancelAppointment() {
        val appointment = _uiState.value.currentAppointment ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val result = bookingRepository.cancelBooking(appointment.id)
                when (result) {
                    is AuthResult.Success -> {
                        Log.d(TAG, "Appointment cancelled successfully")
                        loadUpcomingBookings() // Reload to get next appointment
                    }
                    is AuthResult.Error -> {
                        Log.e(TAG, "Error cancelling appointment: ${result.message}")
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
                Log.e(TAG, "Exception cancelling appointment", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error de conexión"
                    )
                }
            }
        }
    }

    fun onMonthClick() {
        // TODO: Implement month selector
    }
    
    fun isWorkshopManager(): Boolean {
        return userPreferences.isWorkshopManager()
    }

    fun hasWorkshop(): Boolean {
        return userPreferences.hasWorkshop()
    }
    
    private fun formatDate(dateString: String?): String {
        if (dateString.isNullOrBlank()) return "N/A"
        
        return try {
            val formatter = DateTimeFormatter.ISO_DATE_TIME
            val date = LocalDateTime.parse(dateString, formatter)
            val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            date.format(outputFormatter)
        } catch (e: Exception) {
            "N/A"
        }
    }
    
    private fun formatTime(dateString: String?): String {
        if (dateString.isNullOrBlank()) return "N/A"
        
        return try {
            val formatter = DateTimeFormatter.ISO_DATE_TIME
            val date = LocalDateTime.parse(dateString, formatter)
            val outputFormatter = DateTimeFormatter.ofPattern("HH:mm")
            date.format(outputFormatter)
        } catch (e: Exception) {
            "N/A"
        }
    }
}