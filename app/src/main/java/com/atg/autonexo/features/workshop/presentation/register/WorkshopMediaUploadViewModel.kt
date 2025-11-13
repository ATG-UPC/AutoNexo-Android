package com.atg.autonexo.features.workshop.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.features.iam.domain.models.AuthResult
import com.atg.autonexo.features.workshop.domain.repositories.WorkshopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class WorkshopMediaUploadUiState(
    val logoUri: String? = null,
    val photoUris: List<String> = emptyList(),
    val isUploadingLogo: Boolean = false,
    val isUploadingPhotos: Boolean = false,
    val uploadProgress: Int = 0,
    val errorMessage: String? = null,
    val showErrorDialog: Boolean = false,
    val showSuccessDialog: Boolean = false
)

@HiltViewModel
class WorkshopMediaUploadViewModel @Inject constructor(
    private val workshopRepository: WorkshopRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(WorkshopMediaUploadUiState())
    val uiState: StateFlow<WorkshopMediaUploadUiState> = _uiState.asStateFlow()
    
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
    
    fun uploadMedia() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isUploadingLogo = false,
                isUploadingPhotos = false,
                uploadProgress = 0,
                showErrorDialog = false,
                errorMessage = null
            )
            
            try {
                var totalSteps = 0
                var completedSteps = 0
                
                // Contar pasos
                if (_uiState.value.logoUri != null) totalSteps++
                totalSteps += _uiState.value.photoUris.size
                
                // Subir logo si existe
                if (_uiState.value.logoUri != null) {
                    _uiState.value = _uiState.value.copy(isUploadingLogo = true)
                    
                    val logoFile = uriToFile(_uiState.value.logoUri!!)
                    if (logoFile != null && logoFile.exists()) {
                        val result = workshopRepository.uploadLogo(logoFile)
                        
                        when (result) {
                            is AuthResult.Success -> {
                                completedSteps++
                                _uiState.value = _uiState.value.copy(
                                    isUploadingLogo = false,
                                    uploadProgress = (completedSteps * 100) / totalSteps
                                )
                            }
                            is AuthResult.Error -> {
                                _uiState.value = _uiState.value.copy(
                                    isUploadingLogo = false,
                                    errorMessage = result.message,
                                    showErrorDialog = true
                                )
                                return@launch
                            }
                            else -> {
                                _uiState.value = _uiState.value.copy(
                                    isUploadingLogo = false,
                                    errorMessage = "Error desconocido al subir logo",
                                    showErrorDialog = true
                                )
                                return@launch
                            }
                        }
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isUploadingLogo = false,
                            errorMessage = "No se pudo acceder al archivo del logo",
                            showErrorDialog = true
                        )
                        return@launch
                    }
                }
                
                // Subir fotos
                if (_uiState.value.photoUris.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(isUploadingPhotos = true)
                    
                    for (photoUri in _uiState.value.photoUris) {
                        val photoFile = uriToFile(photoUri)
                        if (photoFile != null && photoFile.exists()) {
                            val result = workshopRepository.addPhoto(photoFile)
                            
                            when (result) {
                                is AuthResult.Success -> {
                                    completedSteps++
                                    _uiState.value = _uiState.value.copy(
                                        uploadProgress = (completedSteps * 100) / totalSteps
                                    )
                                }
                                is AuthResult.Error -> {
                                    _uiState.value = _uiState.value.copy(
                                        isUploadingPhotos = false,
                                        errorMessage = result.message,
                                        showErrorDialog = true
                                    )
                                    return@launch
                                }
                                else -> {
                                    _uiState.value = _uiState.value.copy(
                                        isUploadingPhotos = false,
                                        errorMessage = "Error desconocido al subir foto",
                                        showErrorDialog = true
                                    )
                                    return@launch
                                }
                            }
                        }
                    }
                    
                    _uiState.value = _uiState.value.copy(isUploadingPhotos = false)
                }
                
                // Éxito
                _uiState.value = _uiState.value.copy(
                    uploadProgress = 100,
                    showSuccessDialog = true
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUploadingLogo = false,
                    isUploadingPhotos = false,
                    errorMessage = e.message ?: "Error al subir medios",
                    showErrorDialog = true
                )
            }
        }
    }
    
    private fun uriToFile(uriString: String): File? {
        return try {
            // Si es una ruta de archivo directa
            if (uriString.startsWith("file://")) {
                File(uriString.removePrefix("file://"))
            } else if (uriString.startsWith("/")) {
                File(uriString)
            } else {
                // Intentar como ruta relativa
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
}

