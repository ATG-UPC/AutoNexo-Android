package com.atg.autonexo.features.workshop.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.core.data.catalog.CatalogRepository
import com.atg.autonexo.core.data.catalog.models.CapabilityTagDto
import com.atg.autonexo.features.iam.domain.models.AuthResult
import com.atg.autonexo.features.workshop.domain.repositories.WorkshopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkshopTagsSelectionUiState(
    val availableTags: List<CapabilityTagDto> = emptyList(),
    val selectedTags: Set<String> = emptySet(), // Set de códigos de tags seleccionados
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val showErrorDialog: Boolean = false,
    val showSuccessDialog: Boolean = false
)

@HiltViewModel
class WorkshopTagsSelectionViewModel @Inject constructor(
    private val catalogRepository: CatalogRepository,
    private val workshopRepository: WorkshopRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(WorkshopTagsSelectionUiState())
    val uiState: StateFlow<WorkshopTagsSelectionUiState> = _uiState.asStateFlow()
    
    init {
        loadCapabilityTags()
    }
    
    fun loadCapabilityTags() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
                
                val result = catalogRepository.getCapabilityTags(category = null)
                
                when (result) {
                    is AuthResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            availableTags = result.data
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
                            errorMessage = "Error desconocido",
                            showErrorDialog = true
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al cargar tags: ${e.message}",
                    showErrorDialog = false // No mostrar diálogo para permitir continuar
                )
            }
        }
    }
    
    fun toggleTagSelection(tagCode: String) {
        val currentSelected = _uiState.value.selectedTags.toMutableSet()
        if (currentSelected.contains(tagCode)) {
            currentSelected.remove(tagCode)
        } else {
            currentSelected.add(tagCode)
        }
        _uiState.value = _uiState.value.copy(selectedTags = currentSelected)
    }
    
    fun saveTags() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isSaving = true,
                    showErrorDialog = false,
                    errorMessage = null
                )
                
                val tagsList = _uiState.value.selectedTags.toList()
                
                val result = workshopRepository.updateCapabilityTags(tagsList)
                
                when (result) {
                    is AuthResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            showSuccessDialog = true
                        )
                    }
                    is AuthResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            errorMessage = result.message,
                            showErrorDialog = true
                        )
                    }
                    else -> {
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            errorMessage = "Error desconocido",
                            showErrorDialog = true
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = "Error al guardar tags: ${e.message}",
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

