package com.atg.autonexo.features.workshop.presentation.registration.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.features.workshop.domain.models.Location
import com.atg.autonexo.features.workshop.domain.usecases.AddLocationUseCase
import com.atg.autonexo.features.workshop.domain.usecases.GetMyWorkshopUseCase
import com.atg.autonexo.features.workshop.domain.usecases.GetWorkshopLocationsUseCase
import com.atg.autonexo.features.workshop.domain.usecases.UpdateLocationUseCase
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
    private val getMyWorkshopUseCase: GetMyWorkshopUseCase,
    private val getWorkshopLocationsUseCase: GetWorkshopLocationsUseCase,
    private val updateLocationUseCase: UpdateLocationUseCase
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

    fun loadWorkshopLocation(workshopId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            getWorkshopLocationsUseCase(workshopId)
                .onSuccess { locations ->
                    val location = locations.firstOrNull()

                    if (location != null) {
                        val latLng = LatLng(location.latitude, location.longitude)

                        _uiState.value = LocationUiState(
                            locationId = location.id,

                            // Original (para mostrar como "antes")
                            ogStreet   = location.street,
                            ogCity     = location.city,
                            ogState    = location.state,
                            ogZip      = location.zip,
                            ogCountry  = location.country,
                            ogLatitude = location.latitude,
                            ogLongitude = location.longitude,

                            // Editables: empiezan con el valor original
                            street   = location.street,
                            city     = location.city,
                            state    = location.state,
                            zip      = location.zip,
                            country  = location.country,
                            latitude = location.latitude,
                            longitude = location.longitude,
                            selectedLocation = latLng,

                            isLoading = false
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al cargar ubicación del workshop"
                    )
                }
        }
    }

    fun saveLocation(workshopId: Long, onSuccess: (workshopName: String) -> Unit) {
        val state = _uiState.value

        val effectiveStreet   = state.street.ifBlank { state.ogStreet }
        val effectiveCity     = state.city.ifBlank { state.ogCity }
        val effectiveStateVal = state.state.ifBlank { state.ogState }
        val effectiveZip      = state.zip?.ifBlank { state.ogZip }
        val effectiveCountry  = state.country.ifBlank { state.ogCountry }

        val hasNewCoords = state.latitude != 0.0 || state.longitude != 0.0
        val effectiveLatitude  = if (hasNewCoords) state.latitude  else state.ogLatitude  ?: 0.0
        val effectiveLongitude = if (hasNewCoords) state.longitude else state.ogLongitude ?: 0.0

        if (effectiveStreet.isBlank()) {
            _uiState.value = state.copy(errorMessage = "La calle es requerida")
            return
        }

        if (effectiveCity.isBlank()) {
            _uiState.value = state.copy(errorMessage = "La ciudad es requerida")
            return
        }

        if (effectiveStateVal.isBlank()) {
            _uiState.value = state.copy(errorMessage = "El estado es requerido")
            return
        }

        if (effectiveCountry.isBlank()) {
            _uiState.value = state.copy(errorMessage = "El país es requerido")
            return
        }

        if (effectiveLatitude == 0.0 && effectiveLongitude == 0.0) {
            _uiState.value = state.copy(errorMessage = "Debes seleccionar una ubicación en el mapa")
            return
        }

        if (effectiveLatitude !in -90.0..90.0) {
            _uiState.value = state.copy(errorMessage = "Latitud inválida")
            return
        }

        if (effectiveLongitude !in -180.0..180.0) {
            _uiState.value = state.copy(errorMessage = "Longitud inválida")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)

            val location = Location(
                id = state.locationId ?: 0L,              // 0 = crear, !=0 = actualizar
                name = "Location",
                street = effectiveStreet.trim(),
                city = effectiveCity.trim(),
                state = effectiveStateVal.trim(),
                zip = effectiveZip?.trim(),
                country = effectiveCountry.trim(),
                latitude = effectiveLatitude,
                longitude = effectiveLongitude,
                isPrimary = false,
                active = true
            )

            val result = if (state.locationId == null) {
                addLocationUseCase(workshopId, location)
            } else {
                updateLocationUseCase(workshopId, state.locationId, location)
            }

            result
                .onSuccess {
                    getMyWorkshopUseCase()
                        .onSuccess { workshop ->
                            _uiState.value = _uiState.value.copy(isLoading = false)
                            onSuccess(workshop.name)
                        }
                        .onFailure {
                            _uiState.value = _uiState.value.copy(isLoading = false)
                            onSuccess("tu taller")
                        }
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al guardar ubicación"
                    )
                }
        }
    }
}

