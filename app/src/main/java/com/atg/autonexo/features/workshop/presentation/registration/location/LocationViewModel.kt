package com.atg.autonexo.features.workshop.presentation.registration.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.features.workshop.domain.models.Location
import com.atg.autonexo.features.workshop.domain.usecases.AddLocationUseCase
import com.atg.autonexo.features.workshop.domain.usecases.GetMyWorkshopUseCase
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val addLocationUseCase: AddLocationUseCase,
    private val getMyWorkshopUseCase: GetMyWorkshopUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationUiState())
    val uiState: StateFlow<LocationUiState> = _uiState.asStateFlow()

    fun updateStreet(street: String) {
        _uiState.value = _uiState.value.copy(street = street, errorMessage = null)
    }

    fun updateCity(city: String) {
        _uiState.value = _uiState.value.copy(city = city, errorMessage = null)
    }

    fun updateState(state: String) {
        _uiState.value = _uiState.value.copy(state = state, errorMessage = null)
    }

    fun updateZip(zip: String) {
        _uiState.value = _uiState.value.copy(zip = zip, errorMessage = null)
    }

    fun updateCountry(country: String) {
        _uiState.value = _uiState.value.copy(country = country, errorMessage = null)
    }

    fun updateLocation(latLng: LatLng) {
        _uiState.value = _uiState.value.copy(
            selectedLocation = latLng,
            latitude = latLng.latitude,
            longitude = latLng.longitude,
            errorMessage = null
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun addLocation(workshopId: Long, onSuccess: (workshopName: String) -> Unit) {
        val currentState = _uiState.value
        
        // Validaciones
        if (currentState.street.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "La calle es requerida")
            return
        }
        
        if (currentState.city.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "La ciudad es requerida")
            return
        }
        
        if (currentState.state.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "El estado es requerido")
            return
        }
        
        if (currentState.country.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "El país es requerido")
            return
        }
        
        if (currentState.latitude == 0.0 && currentState.longitude == 0.0) {
            _uiState.value = currentState.copy(errorMessage = "Debes seleccionar una ubicación en el mapa")
            return
        }
        
        if (currentState.latitude < -90 || currentState.latitude > 90) {
            _uiState.value = currentState.copy(errorMessage = "Latitud inválida")
            return
        }
        
        if (currentState.longitude < -180 || currentState.longitude > 180) {
            _uiState.value = currentState.copy(errorMessage = "Longitud inválida")
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, errorMessage = null)
            
            val location = Location(
                id = 0, // Se asignará en el backend
                name = "Location",
                street = currentState.street.trim(),
                city = currentState.city.trim(),
                state = currentState.state.trim(),
                zip = currentState.zip.trim().takeIf { it.isNotBlank() },
                country = currentState.country.trim(),
                latitude = currentState.latitude,
                longitude = currentState.longitude,
                isPrimary = false,
                active = true
            )
            
            addLocationUseCase(workshopId, location)
                .onSuccess {
                    // Obtener la información del taller para mostrar en la pantalla de código
                    getMyWorkshopUseCase()
                        .onSuccess { workshop ->
                            _uiState.value = _uiState.value.copy(isLoading = false)
                            onSuccess(workshop.name)
                        }
                        .onFailure {
                            // Si falla, avanzar con un nombre genérico
                            _uiState.value = _uiState.value.copy(isLoading = false)
                            onSuccess("tu taller")
                        }
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al agregar ubicación"
                    )
                }
        }
    }
}

