package com.atg.autonexo.features.workshop.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.core.data.UserPreferences
import com.atg.autonexo.core.data.catalog.CatalogRepository
import com.atg.autonexo.core.data.catalog.models.CapabilityTagDto
import com.atg.autonexo.features.iam.domain.models.AuthResult
import com.atg.autonexo.features.workshop.domain.models.Location
import com.atg.autonexo.features.workshop.domain.repositories.WorkshopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

/**
 * ViewModel compartido para todo el flujo de creación del workshop
 * Mantiene todos los datos hasta que se guarden al final
 */
data class WorkshopCreationFlowState(
    // Step 1: Datos básicos
    val workshopName: String = "",
    val shortDescription: String = "",
    val legalName: String = "",
    val ruc: String = "",
    
    // Step 2: Tags
    val availableTags: List<CapabilityTagDto> = emptyList(),
    val selectedTags: Set<String> = emptySet(),
    val isLoadingTags: Boolean = false,
    
    // Step 3: Media
    val logoUri: String? = null,
    val photoUris: List<String> = emptyList(),
    
    // Step 4: Locations
    val locations: List<LocationInput> = emptyList(),
    
    // Estado general
    val currentStep: Int = 1, // 1=básico, 2=tags, 3=media, 4=locations
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val showErrorDialog: Boolean = false,
    val showSuccessDialog: Boolean = false
) {
    val isStep1Valid: Boolean
        get() {
            val trimmedName = workshopName.trim()
            val nameValid = trimmedName.length >= 3 && trimmedName.length <= 200
            val rucValid = ruc.isEmpty() || (ruc.length == 11 && ruc.all { it.isDigit() })
            val shortDescriptionValid = shortDescription.length <= 500
            val legalNameValid = legalName.length <= 300
            return nameValid && rucValid && shortDescriptionValid && legalNameValid
        }
}

data class LocationInput(
    val street: String,
    val city: String,
    val state: String,
    val zip: String,
    val country: String,
    val latitude: Double?,
    val longitude: Double?
)

@HiltViewModel
class WorkshopCreationFlowViewModel @Inject constructor(
    private val workshopRepository: WorkshopRepository,
    private val catalogRepository: CatalogRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(WorkshopCreationFlowState())
    val uiState: StateFlow<WorkshopCreationFlowState> = _uiState.asStateFlow()
    
    // ========== Step 1: Datos básicos ==========
    
    fun updateWorkshopName(value: String) {
        _uiState.value = _uiState.value.copy(workshopName = value)
    }
    
    fun updateShortDescription(value: String) {
        _uiState.value = _uiState.value.copy(shortDescription = value)
    }
    
    fun updateLegalName(value: String) {
        _uiState.value = _uiState.value.copy(legalName = value)
    }
    
    fun updateRuc(value: String) {
        _uiState.value = _uiState.value.copy(ruc = value)
    }
    
    fun validateAndNextFromStep1(): Boolean {
        val state = _uiState.value
        val trimmedName = state.workshopName.trim()
        
        if (trimmedName.length < 3) {
            _uiState.value = state.copy(
                errorMessage = "El nombre debe tener al menos 3 caracteres",
                showErrorDialog = true
            )
            return false
        }
        
        if (trimmedName.length > 200) {
            _uiState.value = state.copy(
                errorMessage = "El nombre no debe exceder 200 caracteres",
                showErrorDialog = true
            )
            return false
        }
        
        if (state.ruc.isNotBlank() && (state.ruc.length != 11 || !state.ruc.all { it.isDigit() })) {
            _uiState.value = state.copy(
                errorMessage = "El RUC debe tener exactamente 11 dígitos",
                showErrorDialog = true
            )
            return false
        }
        
        _uiState.value = state.copy(currentStep = 2)
        return true
    }
    
    // ========== Guardar solo datos básicos del workshop ==========
    
    fun saveBasicWorkshop() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isSaving = true,
                    showErrorDialog = false,
                    errorMessage = null
                )
                
                val state = _uiState.value
                val ownerUserId = userPreferences.getUserId()
                
                if (ownerUserId == null) {
                    _uiState.value = state.copy(
                        isSaving = false,
                        errorMessage = "Usuario no identificado",
                        showErrorDialog = true
                    )
                    return@launch
                }
                
                val trimmedName = state.workshopName.trim()
                val createResult = workshopRepository.createWorkshop(
                    ownerUserId = ownerUserId,
                    name = trimmedName,
                    shortDescription = state.shortDescription.trim().takeIf { it.isNotBlank() },
                    legalName = state.legalName.trim().takeIf { it.isNotBlank() },
                    ruc = state.ruc.takeIf { it.isNotBlank() }
                )
                
                when (createResult) {
                    is AuthResult.Success -> {
                        userPreferences.setHasWorkshop(true)
                        userPreferences.setIsWorkshopManager(true)
                        
                        _uiState.value = state.copy(
                            isSaving = false,
                            showSuccessDialog = true
                        )
                    }
                    is AuthResult.Error -> {
                        _uiState.value = state.copy(
                            isSaving = false,
                            errorMessage = createResult.message,
                            showErrorDialog = true
                        )
                    }
                    else -> {
                        _uiState.value = state.copy(
                            isSaving = false,
                            errorMessage = "Error desconocido al crear workshop",
                            showErrorDialog = true
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = "Error: ${e.message}",
                    showErrorDialog = true
                )
            }
        }
    }
    
    // ========== Step 2: Tags ==========
    
    fun loadCapabilityTags() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoadingTags = true)
                
                val result = catalogRepository.getCapabilityTags(category = null)
                
                when (result) {
                    is AuthResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoadingTags = false,
                            availableTags = result.data
                        )
                    }
                    is AuthResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoadingTags = false,
                            errorMessage = result.message,
                            showErrorDialog = false // No bloquear el flujo
                        )
                    }
                    else -> {
                        _uiState.value = _uiState.value.copy(
                            isLoadingTags = false,
                            availableTags = emptyList()
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingTags = false,
                    availableTags = emptyList()
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
    
    fun nextFromStep2() {
        _uiState.value = _uiState.value.copy(currentStep = 3)
    }
    
    // ========== Step 3: Media ==========
    
    fun setLogoUri(uri: String?) {
        _uiState.value = _uiState.value.copy(logoUri = uri)
    }
    
    fun addPhotoUri(uri: String) {
        val currentPhotos = _uiState.value.photoUris.toMutableList()
        if (!currentPhotos.contains(uri)) {
            currentPhotos.add(uri)
            _uiState.value = _uiState.value.copy(photoUris = currentPhotos)
        }
    }
    
    fun removePhotoUri(uri: String) {
        val currentPhotos = _uiState.value.photoUris.toMutableList()
        currentPhotos.remove(uri)
        _uiState.value = _uiState.value.copy(photoUris = currentPhotos)
    }
    
    fun nextFromStep3() {
        _uiState.value = _uiState.value.copy(currentStep = 4)
    }
    
    // ========== Step 4: Locations ==========
    
    fun addLocation(location: LocationInput) {
        val currentLocations = _uiState.value.locations.toMutableList()
        currentLocations.add(location)
        _uiState.value = _uiState.value.copy(locations = currentLocations)
    }
    
    fun removeLocation(index: Int) {
        val currentLocations = _uiState.value.locations.toMutableList()
        if (index in currentLocations.indices) {
            currentLocations.removeAt(index)
            _uiState.value = _uiState.value.copy(locations = currentLocations)
        }
    }
    
    // ========== Guardar TODO ==========
    
    fun saveAllWorkshopData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isSaving = true,
                    showErrorDialog = false,
                    errorMessage = null
                )
                
                val state = _uiState.value
                val ownerUserId = userPreferences.getUserId()
                
                if (ownerUserId == null) {
                    _uiState.value = state.copy(
                        isSaving = false,
                        errorMessage = "Usuario no identificado",
                        showErrorDialog = true
                    )
                    return@launch
                }
                
                // 1. Crear workshop básico
                val trimmedName = state.workshopName.trim()
                val createResult = workshopRepository.createWorkshop(
                    ownerUserId = ownerUserId,
                    name = trimmedName,
                    shortDescription = state.shortDescription.trim().takeIf { it.isNotBlank() },
                    legalName = state.legalName.trim().takeIf { it.isNotBlank() },
                    ruc = state.ruc.takeIf { it.isNotBlank() }
                )
                
                when (createResult) {
                    is AuthResult.Success -> {
                        // Workshop creado exitosamente
                        
                        // 2. Guardar tags si hay
                        if (state.selectedTags.isNotEmpty()) {
                            workshopRepository.updateCapabilityTags(state.selectedTags.toList())
                        }
                        
                        // 3. Subir logo si hay
                        if (state.logoUri != null) {
                            val logoFile = uriToFile(state.logoUri)
                            if (logoFile != null && logoFile.exists()) {
                                workshopRepository.uploadLogo(logoFile)
                            }
                        }
                        
                        // 4. Subir fotos si hay
                        for (photoUri in state.photoUris) {
                            val photoFile = uriToFile(photoUri)
                            if (photoFile != null && photoFile.exists()) {
                                workshopRepository.addPhoto(photoFile)
                            }
                        }
                        
                        // 5. Agregar ubicaciones si hay
                        for (location in state.locations) {
                            workshopRepository.addLocation(
                                street = location.street,
                                city = location.city,
                                state = location.state,
                                zip = location.zip,
                                country = location.country,
                                latitude = location.latitude,
                                longitude = location.longitude
                            )
                        }
                        
                        // Actualizar preferencias
                        userPreferences.setHasWorkshop(true)
                        userPreferences.setIsWorkshopManager(true)
                        
                        _uiState.value = state.copy(
                            isSaving = false,
                            showSuccessDialog = true
                        )
                    }
                    is AuthResult.Error -> {
                        _uiState.value = state.copy(
                            isSaving = false,
                            errorMessage = createResult.message,
                            showErrorDialog = true
                        )
                    }
                    else -> {
                        _uiState.value = state.copy(
                            isSaving = false,
                            errorMessage = "Error desconocido al crear workshop",
                            showErrorDialog = true
                        )
                    }
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = "Error: ${e.message}",
                    showErrorDialog = true
                )
            }
        }
    }
    
    private fun uriToFile(uriString: String): File? {
        return try {
            if (uriString.startsWith("file://")) {
                File(uriString.removePrefix("file://"))
            } else if (uriString.startsWith("/")) {
                File(uriString)
            } else {
                File(uriString)
            }
        } catch (e: Exception) {
            null
        }
    }
    
    fun dismissSuccessDialog() {
        _uiState.value = _uiState.value.copy(showSuccessDialog = false)
    }
    
    fun dismissErrorDialog() {
        _uiState.value = _uiState.value.copy(showErrorDialog = false)
    }
    
    fun goBack() {
        if (_uiState.value.currentStep > 1) {
            _uiState.value = _uiState.value.copy(currentStep = _uiState.value.currentStep - 1)
        }
    }
}

