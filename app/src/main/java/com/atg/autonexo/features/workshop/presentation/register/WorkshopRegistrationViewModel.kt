package com.atg.autonexo.features.workshop.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkshopRegistrationUiState(
    // Step 1 fields
    val workshopName: String = "",
    val ruc: String = "",
    val district: String = "",
    val city: String = "",
    val address: String = "",
    val logoUri: String? = null,
    val workshopImageUri: String? = null,
    val services: List<String> = listOf("Tire change"),
    
    // Step 2 fields
    val mondayStart: String = "08:00",
    val mondayEnd: String = "18:00",
    val mondayFreeDay: Boolean = false,
    val tuesdayStart: String = "08:00",
    val tuesdayEnd: String = "18:00",
    val tuesdayFreeDay: Boolean = false,
    val wednesdayStart: String = "08:00",
    val wednesdayEnd: String = "18:00",
    val wednesdayFreeDay: Boolean = false,
    val thursdayStart: String = "08:00",
    val thursdayEnd: String = "18:00",
    val thursdayFreeDay: Boolean = false,
    val fridayStart: String = "08:00",
    val fridayEnd: String = "18:00",
    val fridayFreeDay: Boolean = false,
    val saturdayStart: String = "08:00",
    val saturdayEnd: String = "18:00",
    val saturdayFreeDay: Boolean = false,
    val sundayStart: String = "08:00",
    val sundayEnd: String = "18:00",
    val sundayFreeDay: Boolean = false,
    val open24Hours: Boolean = false,
    val description: String = "",
    
    // UI state
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showSuccessDialog: Boolean = false,
    val showErrorDialog: Boolean = false
) {
    val isStep1Valid: Boolean
        get() = workshopName.isNotBlank() &&
                (ruc.isEmpty() || (ruc.length >= 8 && ruc.all { it.isDigit() }))
    
    val isStep2Valid: Boolean
        get() {
            if (open24Hours) return true
            
            val days = listOf(
                Triple(mondayFreeDay, mondayStart, mondayEnd),
                Triple(tuesdayFreeDay, tuesdayStart, tuesdayEnd),
                Triple(wednesdayFreeDay, wednesdayStart, wednesdayEnd),
                Triple(thursdayFreeDay, thursdayStart, thursdayEnd),
                Triple(fridayFreeDay, fridayStart, fridayEnd),
                Triple(saturdayFreeDay, saturdayStart, saturdayEnd),
                Triple(sundayFreeDay, sundayStart, sundayEnd)
            )
            
            return days.all { (isFree, start, end) ->
                isFree || start < end
            }
        }
}

@HiltViewModel
class WorkshopRegistrationViewModel @Inject constructor(
    private val userPreferences: com.atg.autonexo.core.data.UserPreferences,
    private val workshopRepository: com.atg.autonexo.features.workshop.domain.repositories.WorkshopRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(WorkshopRegistrationUiState())
    val uiState: StateFlow<WorkshopRegistrationUiState> = _uiState.asStateFlow()
    
    // Step 1 updates
    fun updateWorkshopName(value: String) {
        _uiState.value = _uiState.value.copy(workshopName = value)
    }
    
    fun updateRuc(value: String) {
        if (value.all { it.isDigit() } && value.length <= 11) {
            _uiState.value = _uiState.value.copy(ruc = value)
        }
    }
    
    fun updateDistrict(value: String) {
        _uiState.value = _uiState.value.copy(district = value)
    }
    
    fun updateCity(value: String) {
        _uiState.value = _uiState.value.copy(city = value)
    }
    
    fun updateAddress(value: String) {
        _uiState.value = _uiState.value.copy(address = value)
    }
    
    fun updateLogoUri(uri: String?) {
        _uiState.value = _uiState.value.copy(logoUri = uri)
    }
    
    fun updateWorkshopImageUri(uri: String?) {
        _uiState.value = _uiState.value.copy(workshopImageUri = uri)
    }
    
    fun addService(service: String) {
        val currentServices = _uiState.value.services
        if (service.isNotBlank() && !currentServices.contains(service)) {
            _uiState.value = _uiState.value.copy(services = currentServices + service)
        }
    }
    
    fun removeService(service: String) {
        _uiState.value = _uiState.value.copy(
            services = _uiState.value.services.filter { it != service }
        )
    }
    
    // Step 2 updates
    fun updateDaySchedule(day: String, start: String? = null, end: String? = null, isFree: Boolean? = null) {
        _uiState.value = when (day.lowercase()) {
            "monday" -> _uiState.value.copy(
                mondayStart = start ?: _uiState.value.mondayStart,
                mondayEnd = end ?: _uiState.value.mondayEnd,
                mondayFreeDay = isFree ?: _uiState.value.mondayFreeDay
            )
            "tuesday" -> _uiState.value.copy(
                tuesdayStart = start ?: _uiState.value.tuesdayStart,
                tuesdayEnd = end ?: _uiState.value.tuesdayEnd,
                tuesdayFreeDay = isFree ?: _uiState.value.tuesdayFreeDay
            )
            "wednesday" -> _uiState.value.copy(
                wednesdayStart = start ?: _uiState.value.wednesdayStart,
                wednesdayEnd = end ?: _uiState.value.wednesdayEnd,
                wednesdayFreeDay = isFree ?: _uiState.value.wednesdayFreeDay
            )
            "thursday" -> _uiState.value.copy(
                thursdayStart = start ?: _uiState.value.thursdayStart,
                thursdayEnd = end ?: _uiState.value.thursdayEnd,
                thursdayFreeDay = isFree ?: _uiState.value.thursdayFreeDay
            )
            "friday" -> _uiState.value.copy(
                fridayStart = start ?: _uiState.value.fridayStart,
                fridayEnd = end ?: _uiState.value.fridayEnd,
                fridayFreeDay = isFree ?: _uiState.value.fridayFreeDay
            )
            "saturday" -> _uiState.value.copy(
                saturdayStart = start ?: _uiState.value.saturdayStart,
                saturdayEnd = end ?: _uiState.value.saturdayEnd,
                saturdayFreeDay = isFree ?: _uiState.value.saturdayFreeDay
            )
            "sunday" -> _uiState.value.copy(
                sundayStart = start ?: _uiState.value.sundayStart,
                sundayEnd = end ?: _uiState.value.sundayEnd,
                sundayFreeDay = isFree ?: _uiState.value.sundayFreeDay
            )
            else -> _uiState.value
        }
    }
    
    fun updateOpen24Hours(value: Boolean) {
        _uiState.value = _uiState.value.copy(open24Hours = value)
    }
    
    fun updateDescription(value: String) {
        _uiState.value = _uiState.value.copy(description = value)
    }
    
    fun registerWorkshop() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                showErrorDialog = false,
                errorMessage = null
            )
            
            try {
                // Llamar al repositorio para crear el workshop
                val result = workshopRepository.createWorkshop(
                    name = _uiState.value.workshopName,
                    description = _uiState.value.description.ifBlank { "Taller mecánico especializado" },
                    contactEmail = userPreferences.getUserEmail() ?: "contacto@taller.com",
                    contactPhone = _uiState.value.ruc.ifBlank { "999999999" },
                    address = _uiState.value.address,
                    district = _uiState.value.district,
                    city = _uiState.value.city,
                    latitude = -12.0464, // Lima, Perú - coordenadas por defecto
                    longitude = -77.0428
                )
                
                when (result) {
                    is com.atg.autonexo.features.iam.domain.models.AuthResult.Success -> {
                        // Éxito - persistir que ya tiene workshop
                        userPreferences.setHasWorkshop(true)
                        userPreferences.setIsWorkshopManager(true)
                        
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            showSuccessDialog = true
                        )
                    }
                    is com.atg.autonexo.features.iam.domain.models.AuthResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            showErrorDialog = true,
                            errorMessage = result.message
                        )
                    }
                    else -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            showErrorDialog = true,
                            errorMessage = "Error desconocido"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    showErrorDialog = true,
                    errorMessage = e.message ?: "Error al crear taller"
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

