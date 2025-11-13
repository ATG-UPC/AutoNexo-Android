package com.atg.autonexo.features.workshop.presentation.edit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.atg.autonexo.features.workshop.domain.repositories.WorkshopRepository
import com.atg.autonexo.features.iam.domain.models.AuthResult

data class WorkshopEditUiState(
    val workshopName: String = "",
    val shortDescription: String = "",
    val legalName: String = "",
    val ruc: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val showErrorDialog: Boolean = false,
    val showSuccessDialog: Boolean = false
) {
    val isValid: Boolean
        get() {
            val trimmedName = workshopName.trim()
            val nameValid = trimmedName.length >= 3 && trimmedName.length <= 200
            val rucValid = ruc.isEmpty() || (ruc.length == 11 && ruc.all { it.isDigit() })
            val shortDescriptionValid = shortDescription.length <= 500
            val legalNameValid = legalName.length <= 300
            return nameValid && rucValid && shortDescriptionValid && legalNameValid
        }
}

@HiltViewModel
class WorkshopEditViewModel @Inject constructor(
    private val workshopRepository: WorkshopRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkshopEditUiState())
    val uiState: StateFlow<WorkshopEditUiState> = _uiState.asStateFlow()

    init {
        loadWorkshopData()
    }

    private fun loadWorkshopData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                
                val result = workshopRepository.getMyWorkshop()
                
                when (result) {
                    is AuthResult.Success -> {
                        val workshop = result.data
                        _uiState.value = _uiState.value.copy(
                            workshopName = workshop.name,
                            shortDescription = workshop.description, // El mapper ya usa shortDescription
                            legalName = workshop.legalName ?: "",
                            ruc = workshop.ruc ?: "",
                            isLoading = false
                        )
                    }
                    is AuthResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = result.message,
                            showErrorDialog = true
                        )
                    }
                    else -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Error al cargar datos del taller",
                            showErrorDialog = true
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("WorkshopEditVM", "Error loading workshop data", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error: ${e.message}",
                    showErrorDialog = true
                )
            }
        }
    }

    fun updateWorkshopName(value: String) {
        val limitedValue = if (value.length > 200) value.take(200) else value
        _uiState.value = _uiState.value.copy(workshopName = limitedValue)
    }

    fun updateShortDescription(value: String) {
        val limitedValue = if (value.length > 500) value.take(500) else value
        _uiState.value = _uiState.value.copy(shortDescription = limitedValue)
    }

    fun updateLegalName(value: String) {
        val limitedValue = if (value.length > 300) value.take(300) else value
        _uiState.value = _uiState.value.copy(legalName = limitedValue)
    }

    fun updateRuc(value: String) {
        if (value.all { it.isDigit() } && value.length <= 11) {
            _uiState.value = _uiState.value.copy(ruc = value)
        }
    }

    fun saveWorkshop() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isSaving = true,
                    showErrorDialog = false,
                    errorMessage = null
                )

                val state = _uiState.value
                val trimmedName = state.workshopName.trim()
                
                // Validaciones
                if (trimmedName.length < 3) {
                    _uiState.value = state.copy(
                        isSaving = false,
                        errorMessage = "El nombre debe tener al menos 3 caracteres",
                        showErrorDialog = true
                    )
                    return@launch
                }
                
                if (trimmedName.length > 200) {
                    _uiState.value = state.copy(
                        isSaving = false,
                        errorMessage = "El nombre no puede tener más de 200 caracteres",
                        showErrorDialog = true
                    )
                    return@launch
                }
                
                if (state.ruc.isNotBlank() && (state.ruc.length != 11 || !state.ruc.all { it.isDigit() })) {
                    _uiState.value = state.copy(
                        isSaving = false,
                        errorMessage = "El RUC debe tener exactamente 11 dígitos",
                        showErrorDialog = true
                    )
                    return@launch
                }

                val result = workshopRepository.updateWorkshop(
                    name = trimmedName,
                    shortDescription = state.shortDescription.trim().takeIf { it.isNotBlank() },
                    description = null,
                    contactEmail = null,
                    contactPhone = null
                )

                when (result) {
                    is AuthResult.Success -> {
                        _uiState.value = state.copy(
                            isSaving = false,
                            showSuccessDialog = true
                        )
                    }
                    is AuthResult.Error -> {
                        _uiState.value = state.copy(
                            isSaving = false,
                            errorMessage = result.message,
                            showErrorDialog = true
                        )
                    }
                    else -> {
                        _uiState.value = state.copy(
                            isSaving = false,
                            errorMessage = "Error desconocido al actualizar taller",
                            showErrorDialog = true
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("WorkshopEditVM", "Error saving workshop", e)
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = "Error: ${e.message}",
                    showErrorDialog = true
                )
            }
        }
    }

    fun dismissErrorDialog() {
        _uiState.value = _uiState.value.copy(showErrorDialog = false, errorMessage = null)
    }

    fun dismissSuccessDialog() {
        _uiState.value = _uiState.value.copy(showSuccessDialog = false)
    }
}

