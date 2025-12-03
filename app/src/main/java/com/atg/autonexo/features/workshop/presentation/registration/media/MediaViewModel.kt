package com.atg.autonexo.features.workshop.presentation.registration.media

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.features.workshop.domain.usecases.GetMyWorkshopUseCase
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
    private val uploadPhotoUseCase: UploadPhotoUseCase,
    private val getMyWorkshopUseCase: GetMyWorkshopUseCase
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
        val state = _uiState.value
        val currentLocal = state.photoUris.toMutableList()
        val totalExisting = state.photoUrls.size + currentLocal.size

        if (totalExisting < 10) {
            currentLocal.add(uri)
            _uiState.value = state.copy(photoUris = currentLocal, errorMessage = null)
        } else {
            _uiState.value = state.copy(errorMessage = "Máximo 10 fotos permitidas")
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

    fun loadWorkshopMedia() {
        viewModelScope.launch {
            getMyWorkshopUseCase()
                .onSuccess { workshop ->
                    _uiState.value = _uiState.value.copy(
                        logoUrl = workshop.logoUrl,
                        photoUrls = workshop.photoUrls,
                        errorMessage = null
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = e.message ?: "Error al cargar media del taller"
                    )
                }
        }
    }

    fun uploadMedia(workshopId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val before = _uiState.value

            if (before.logoUri == null && before.photoUris.isEmpty()) {
                onSuccess()
                return@launch
            }

            _uiState.value = before.copy(isUploading = true, errorMessage = null)

            try {
                var state = _uiState.value

                state.logoUri?.let { logoUri ->
                    uploadLogoUseCase(workshopId, logoUri)
                        .onSuccess { url ->
                            state = state.copy(
                                logoUrl = url,
                                logoUri = null
                            )
                            _uiState.value = state
                        }
                        .onFailure { ex ->
                            _uiState.value = state.copy(
                                isUploading = false,
                                errorMessage = "Error al subir logo: ${ex.message}"
                            )
                            return@launch
                        }
                }

                val uploadedPhotoUrls = mutableListOf<String>()
                for (photoUri in state.photoUris) {
                    uploadPhotoUseCase(workshopId, photoUri)
                        .onSuccess { url ->
                            uploadedPhotoUrls.add(url)
                        }
                        .onFailure { ex ->
                            _uiState.value = state.copy(
                                isUploading = false,
                                errorMessage = "Error al subir foto: ${ex.message}"
                            )
                            return@launch
                        }
                }

                val allPhotoUrls = state.photoUrls + uploadedPhotoUrls

                _uiState.value = state.copy(
                    isUploading = false,
                    photoUrls = allPhotoUrls,
                    photoUris = emptyList()
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

