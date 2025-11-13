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
    // Step 1 fields - Campos requeridos para crear workshop según backend
    val workshopName: String = "",
    val shortDescription: String = "",
    val legalName: String = "",
    val ruc: String = "",
    
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
        get() {
            // name: requerido, mínimo 3 caracteres, máximo 200 caracteres (usar trim para eliminar espacios)
            val trimmedName = workshopName.trim()
            val nameValid = trimmedName.length >= 3 && trimmedName.length <= 200
            
            // ruc: opcional, si se proporciona debe tener exactamente 11 dígitos
            val rucValid = ruc.isEmpty() || (ruc.length == 11 && ruc.all { it.isDigit() })
            
            // shortDescription: opcional, máximo 500 caracteres
            val shortDescriptionValid = shortDescription.length <= 500
            
            // legalName: opcional, máximo 300 caracteres
            val legalNameValid = legalName.length <= 300
            
            return nameValid && rucValid && shortDescriptionValid && legalNameValid
        }
    
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
        // Limitar a 200 caracteres según validación del backend
        val limitedValue = if (value.length > 200) value.take(200) else value
        _uiState.value = _uiState.value.copy(workshopName = limitedValue)
    }
    
    fun updateShortDescription(value: String) {
        // Limitar a 500 caracteres según validación del backend
        val limitedValue = if (value.length > 500) value.take(500) else value
        _uiState.value = _uiState.value.copy(shortDescription = limitedValue)
    }
    
    fun updateLegalName(value: String) {
        // Limitar a 300 caracteres según validación del backend
        val limitedValue = if (value.length > 300) value.take(300) else value
        _uiState.value = _uiState.value.copy(legalName = limitedValue)
    }
    
    fun updateRuc(value: String) {
        // Solo permitir dígitos y máximo 11 caracteres (exactamente 11 según backend)
        if (value.all { it.isDigit() } && value.length <= 11) {
            _uiState.value = _uiState.value.copy(ruc = value)
        }
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
                // Obtener ownerUserId del usuario autenticado
                val ownerUserId = userPreferences.getUserId()
                if (ownerUserId == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        showErrorDialog = true,
                        errorMessage = "No se pudo obtener el ID del usuario. Por favor, inicia sesión nuevamente."
                    )
                    return@launch
                }
                
                // Validar que el nombre tenga al menos 3 caracteres (usar trim para eliminar espacios)
                val trimmedName = _uiState.value.workshopName.trim()
                if (trimmedName.length < 3) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        showErrorDialog = true,
                        errorMessage = "El nombre del taller debe tener al menos 3 caracteres"
                    )
                    return@launch
                }
                
                if (trimmedName.length > 200) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        showErrorDialog = true,
                        errorMessage = "El nombre del taller no puede tener más de 200 caracteres"
                    )
                    return@launch
                }
                
                // Validar RUC si se proporciona
                if (_uiState.value.ruc.isNotBlank() && (_uiState.value.ruc.length != 11 || !_uiState.value.ruc.all { it.isDigit() })) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        showErrorDialog = true,
                        errorMessage = "El RUC debe tener exactamente 11 dígitos"
                    )
                    return@launch
                }
                
                // Llamar al repositorio para crear el workshop (usar trimmedName)
                val result = workshopRepository.createWorkshop(
                    ownerUserId = ownerUserId,
                    name = trimmedName,
                    shortDescription = _uiState.value.shortDescription.trim().takeIf { it.isNotBlank() },
                    legalName = _uiState.value.legalName.trim().takeIf { it.isNotBlank() },
                    ruc = _uiState.value.ruc.takeIf { it.isNotBlank() }
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

