package com.atg.autonexo.features.workshop.presentation.registration.basicinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.features.auth.domain.usecases.GetCurrentUserUseCase
import com.atg.autonexo.features.workshop.domain.models.CreateWorkshopRequest
import com.atg.autonexo.features.workshop.domain.usecases.CreateWorkshopUseCase
import com.atg.autonexo.features.workshop.domain.usecases.GetMyWorkshopUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BasicInfoViewModel @Inject constructor(
    private val createWorkshopUseCase: CreateWorkshopUseCase,
    private val getMyWorkshopUseCase: GetMyWorkshopUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BasicInfoUiState())
    val uiState: StateFlow<BasicInfoUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name, errorMessage = null)
    }

    fun updateShortDescription(shortDescription: String) {
        _uiState.value = _uiState.value.copy(shortDescription = shortDescription, errorMessage = null)
    }

    fun updateLegalName(legalName: String) {
        _uiState.value = _uiState.value.copy(legalName = legalName, errorMessage = null)
    }

    fun updateRuc(ruc: String) {
        _uiState.value = _uiState.value.copy(ruc = ruc, errorMessage = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun createWorkshop(onSuccess: (workshopId: Long) -> Unit) {
        val currentState = _uiState.value
        
        // Validaciones
        if (currentState.name.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "El nombre es requerido")
            return
        }
        
        if (currentState.name.length < 3) {
            _uiState.value = currentState.copy(errorMessage = "El nombre debe tener al menos 3 caracteres")
            return
        }
        
        if (currentState.name.length > 200) {
            _uiState.value = currentState.copy(errorMessage = "El nombre no puede exceder 200 caracteres")
            return
        }
        
        if (currentState.shortDescription.isNotBlank() && currentState.shortDescription.length > 500) {
            _uiState.value = currentState.copy(errorMessage = "La descripción corta no puede exceder 500 caracteres")
            return
        }
        
        if (currentState.legalName.isNotBlank() && currentState.legalName.length > 300) {
            _uiState.value = currentState.copy(errorMessage = "El nombre legal no puede exceder 300 caracteres")
            return
        }
        
        if (currentState.ruc.isNotBlank()) {
            if (currentState.ruc.length != 11 || !currentState.ruc.all { it.isDigit() }) {
                _uiState.value = currentState.copy(errorMessage = "El RUC debe tener exactamente 11 dígitos numéricos")
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, errorMessage = null)
            
            // Obtener el usuario autenticado primero
            getCurrentUserUseCase()
                .onSuccess { user ->
                    val request = CreateWorkshopRequest(
                        ownerUserId = user.id,
                        name = currentState.name.trim(),
                        shortDescription = currentState.shortDescription.trim().takeIf { it.isNotBlank() },
                        legalName = currentState.legalName.trim().takeIf { it.isNotBlank() },
                        ruc = currentState.ruc.trim().takeIf { it.isNotBlank() }
                    )
                    
                    createWorkshopUseCase(request)
                        .onSuccess { workshop ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isSuccess = true,
                                workshopId = workshop.id
                            )
                            onSuccess(workshop.id)
                        }
                        .onFailure { exception ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = exception.message ?: "Error al crear workshop"
                            )
                        }
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al obtener usuario: ${exception.message}"
                    )
                }
        }
    }

    fun checkExistingWorkshop(onWorkshopExists: (workshopId: Long) -> Unit, onNoWorkshop: () -> Unit) {
        viewModelScope.launch {
            getMyWorkshopUseCase()
                .onSuccess { workshop ->
                    _uiState.value = _uiState.value.copy(workshopId = workshop.id)
                    onWorkshopExists(workshop.id)
                }
                .onFailure {
                    onNoWorkshop()
                }
        }
    }
}

