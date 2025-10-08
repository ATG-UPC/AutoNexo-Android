package com.atg.autonexo.features.vehiclemaintenance.presentation.vehicle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.features.vehiclemaintenance.presentation.vehicle.models.VehicleUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class VehicleDetailEvent {
    object SaveSuccess : VehicleDetailEvent()
}

@HiltViewModel
class VehicleDetailViewModel @Inject constructor(
    // TODO: Inyectar repositorio
) : ViewModel() {

    private val _uiState = MutableStateFlow(VehicleDetailUiState())
    val uiState: StateFlow<VehicleDetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<VehicleDetailEvent>()
    val events: SharedFlow<VehicleDetailEvent> = _events.asSharedFlow()

    fun loadVehicle(vehicleId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Mock data
            val mockVehicle = VehicleUi(
                vehicleId = vehicleId,
                licensePlate = "AB1-364",
                brand = "Nissan",
                model = "Sentra",
                year = 2018,
                ownerId = "owner1",
                photoUrl = null,
                maintenanceLogUrl = null
            )
            
            _uiState.update {
                it.copy(
                    isLoading = false,
                    vehicle = mockVehicle,
                    brand = mockVehicle.brand,
                    model = mockVehicle.model,
                    year = mockVehicle.year.toString(),
                    licensePlate = mockVehicle.licensePlate
                )
            }
            validateAll()
        }
    }

    fun updateBrand(value: String) {
        _uiState.update { it.copy(brand = value, brandError = null) }
        validateAll()
    }

    fun updateModel(value: String) {
        _uiState.update { it.copy(model = value, modelError = null) }
        validateAll()
    }

    fun updateYear(value: String) {
        _uiState.update { it.copy(year = value, yearError = null) }
        validateAll()
    }

    fun updateLicensePlate(value: String) {
        _uiState.update { it.copy(licensePlate = value.uppercase(), licensePlateError = null) }
        validateAll()
    }

    private fun validateAll() {
        val state = _uiState.value
        
        val brandError = if (state.brand.isBlank()) "Brand is required" else null
        val modelError = if (state.model.isBlank()) "Model is required" else null
        val yearError = when {
            state.year.isBlank() -> "Year is required"
            state.year.toIntOrNull() == null -> "Invalid year"
            state.year.toInt() < 1900 || state.year.toInt() > 2030 -> "Year out of range"
            else -> null
        }
        val licensePlateError = when {
            state.licensePlate.isBlank() -> "License plate is required"
            !state.licensePlate.matches(Regex("^[A-Z0-9\\-]{3,10}$")) -> "Invalid format"
            else -> null
        }

        _uiState.update {
            it.copy(
                brandError = brandError,
                modelError = modelError,
                yearError = yearError,
                licensePlateError = licensePlateError,
                isSaveEnabled = brandError == null && modelError == null && 
                               yearError == null && licensePlateError == null
            )
        }
    }

    fun saveVehicle() {
        if (!_uiState.value.isSaveEnabled) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // TODO: Guardar en repositorio
            kotlinx.coroutines.delay(1000)
            
            _uiState.update { it.copy(isLoading = false) }
            _events.emit(VehicleDetailEvent.SaveSuccess)
        }
    }
}

