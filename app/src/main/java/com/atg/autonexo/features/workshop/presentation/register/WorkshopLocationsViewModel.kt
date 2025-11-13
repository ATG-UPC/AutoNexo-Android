package com.atg.autonexo.features.workshop.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.features.iam.domain.models.AuthResult
import com.atg.autonexo.features.workshop.domain.models.Location
import com.atg.autonexo.features.workshop.domain.repositories.WorkshopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LocationFormState(
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val zip: String = "",
    val country: String = "",
    val latitude: String = "",
    val longitude: String = ""
) {
    val isValid: Boolean
        get() = street.isNotBlank() && 
                city.isNotBlank() && 
                state.isNotBlank() && 
                zip.isNotBlank() && 
                country.isNotBlank()
}

data class WorkshopLocationsUiState(
    val locations: List<Location> = emptyList(),
    val currentForm: LocationFormState = LocationFormState(),
    val isAdding: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showErrorDialog: Boolean = false,
    val showSuccessDialog: Boolean = false
)

@HiltViewModel
class WorkshopLocationsViewModel @Inject constructor(
    private val workshopRepository: WorkshopRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(WorkshopLocationsUiState())
    val uiState: StateFlow<WorkshopLocationsUiState> = _uiState.asStateFlow()
    
    fun updateStreet(value: String) {
        _uiState.value = _uiState.value.copy(
            currentForm = _uiState.value.currentForm.copy(street = value)
        )
    }
    
    fun updateCity(value: String) {
        _uiState.value = _uiState.value.copy(
            currentForm = _uiState.value.currentForm.copy(city = value)
        )
    }
    
    fun updateState(value: String) {
        _uiState.value = _uiState.value.copy(
            currentForm = _uiState.value.currentForm.copy(state = value)
        )
    }
    
    fun updateZip(value: String) {
        _uiState.value = _uiState.value.copy(
            currentForm = _uiState.value.currentForm.copy(zip = value)
        )
    }
    
    fun updateCountry(value: String) {
        _uiState.value = _uiState.value.copy(
            currentForm = _uiState.value.currentForm.copy(country = value)
        )
    }
    
    fun updateLatitude(value: String) {
        _uiState.value = _uiState.value.copy(
            currentForm = _uiState.value.currentForm.copy(latitude = value)
        )
    }
    
    fun updateLongitude(value: String) {
        _uiState.value = _uiState.value.copy(
            currentForm = _uiState.value.currentForm.copy(longitude = value)
        )
    }
    
    fun addLocation() {
        viewModelScope.launch {
            try {
                val form = _uiState.value.currentForm
                
                if (!form.isValid) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Por favor completa todos los campos requeridos",
                        showErrorDialog = true
                    )
                    return@launch
                }
                
                _uiState.value = _uiState.value.copy(
                    isAdding = true,
                    showErrorDialog = false,
                    errorMessage = null
                )
                
                val latitude = form.latitude.toDoubleOrNull()
                val longitude = form.longitude.toDoubleOrNull()
                
                val result = workshopRepository.addLocation(
                    street = form.street.trim(),
                    city = form.city.trim(),
                    state = form.state.trim(),
                    zip = form.zip.trim(),
                    country = form.country.trim(),
                    latitude = latitude,
                    longitude = longitude
                )
                
                when (result) {
                    is AuthResult.Success -> {
                        val newLocation = result.data
                        _uiState.value = _uiState.value.copy(
                            isAdding = false,
                            locations = _uiState.value.locations + newLocation,
                            currentForm = LocationFormState(), // Reset form
                            showSuccessDialog = true
                        )
                    }
                    is AuthResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isAdding = false,
                            errorMessage = result.message,
                            showErrorDialog = true
                        )
                    }
                    else -> {
                        _uiState.value = _uiState.value.copy(
                            isAdding = false,
                            errorMessage = "Error desconocido",
                            showErrorDialog = true
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isAdding = false,
                    errorMessage = "Error al agregar ubicación: ${e.message}",
                    showErrorDialog = true
                )
            }
        }
    }
    
    fun removeLocation(locationId: Long) {
        viewModelScope.launch {
            try {
                val result = workshopRepository.deleteLocation(locationId)
                
                when (result) {
                    is AuthResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            locations = _uiState.value.locations.filter { it.id != locationId }
                        )
                    }
                    is AuthResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = result.message,
                            showErrorDialog = true
                        )
                    }
                    else -> {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "Error al eliminar ubicación",
                            showErrorDialog = true
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al eliminar ubicación: ${e.message}",
                    showErrorDialog = true
                )
            }
        }
    }
    
    fun dismissSuccessDialog() {
        _uiState.value = _uiState.value.copy(showSuccessDialog = false)
    }
    
    fun dismissErrorDialog() {
        _uiState.value = _uiState.value.copy(showErrorDialog = false)
    }
}

