package com.atg.autonexo.features.matchingbooking.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.core.data.UserPreferences
import com.atg.autonexo.features.matchingbooking.presentation.dashboard.models.AppointmentUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
    private val userPreferences: UserPreferences
    // TODO: Inyectar repositorios cuando estén listos
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        viewModelScope.launch {
            // Mock data para demostración
            _uiState.update {
                it.copy(
                    currentAppointment = AppointmentUi(
                        id = "1",
                        date = "20/10/205",
                        time = "13:00",
                        owner = "Sergio Iglesias",
                        mechanic = "Arturo Gonzáles"
                    )
                )
            }
        }
    }

    fun onPostponeAppointment() {
        // TODO: Implementar lógica de posponer cita
    }

    fun onCancelAppointment() {
        // TODO: Implementar lógica de cancelar cita
    }

    fun onMonthClick() {
        // TODO: Implementar selector de mes
    }
    
    fun isWorkshopManager(): Boolean {
        return userPreferences.isWorkshopManager()
    }
}