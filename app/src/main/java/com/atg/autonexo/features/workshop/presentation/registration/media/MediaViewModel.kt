package com.atg.autonexo.features.workshop.presentation.registration.media

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.features.workshop.domain.usecases.UploadLogoUseCase
import com.atg.autonexo.features.workshop.domain.usecases.UploadPhotoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(
    private val uploadLogoUseCase: UploadLogoUseCase,
    private val uploadPhotoUseCase: UploadPhotoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MediaUiState())
    val uiState: StateFlow<MediaUiState> = _uiState.asStateFlow()

    fun setLogo(uri: Uri) {
        _uiState.value = _uiState.value.copy(logoUri = uri, errorMessage = null)
    }

    fun removeLogo() {
        _uiState.value = _uiState.value.copy(logoUri = null, errorMessage = null)
    }

    fun addPhoto(uri: Uri) {
        val currentPhotos = _uiState.value.photoUris.toMutableList()
        if (currentPhotos.size < 10) {
            currentPhotos.add(uri)
            _uiState.value = _uiState.value.copy(photoUris = currentPhotos, errorMessage = null)
        } else {
            _uiState.value = _uiState.value.copy(errorMessage = "Máximo 10 fotos permitidas")
        }
    }

    fun removePhoto(index: Int) {
        val currentPhotos = _uiState.value.photoUris.toMutableList()
        if (index in currentPhotos.indices) {
            currentPhotos.removeAt(index)
            _uiState.value = _uiState.value.copy(photoUris = currentPhotos, errorMessage = null)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun uploadMedia(workshopId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploading = true, errorMessage = null)
            
            try {
                // Subir logo si existe
                _uiState.value.logoUri?.let { logoUri ->
                    uploadLogoUseCase(workshopId, logoUri)
                        .onSuccess { url ->
                            _uiState.value = _uiState.value.copy(logoUrl = url)
                        }
                        .onFailure { exception ->
                            _uiState.value = _uiState.value.copy(
                                isUploading = false,
                                errorMessage = "Error al subir logo: ${exception.message}"
                            )
                            return@launch
                        }
                }
                
                // Subir fotos una por una
                val uploadedPhotoUrls = mutableListOf<String>()
                for (photoUri in _uiState.value.photoUris) {
                    uploadPhotoUseCase(workshopId, photoUri)
                        .onSuccess { url ->
                            uploadedPhotoUrls.add(url)
                        }
                        .onFailure { exception ->
                            _uiState.value = _uiState.value.copy(
                                isUploading = false,
                                errorMessage = "Error al subir foto: ${exception.message}"
                            )
                            return@launch
                        }
                }
                
                _uiState.value = _uiState.value.copy(
                    isUploading = false,
                    photoUrls = uploadedPhotoUrls
                )
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUploading = false,
                    errorMessage = "Error inesperado: ${e.message}"
                )
            }
        }
    }
}

