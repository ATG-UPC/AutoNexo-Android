package com.atg.autonexo.features.profile.presentation.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.features.auth.domain.usecases.GetCurrentUserUseCase
import com.atg.autonexo.features.profile.domain.usecases.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            getCurrentUserUseCase()
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(
                        fullName = "${user.firstName} ${user.lastName}",
                        email = user.email,
                        phoneNumber = user.phoneNumber,
                        isLoading = false
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al cargar datos del usuario"
                    )
                }
        }
    }

    fun updateFullName(fullName: String) {
        _uiState.value = _uiState.value.copy(fullName = fullName, errorMessage = null)
    }

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }

    fun updatePhoneNumber(phoneNumber: String) {
        _uiState.value = _uiState.value.copy(phoneNumber = phoneNumber, errorMessage = null)
    }

    fun saveProfile(onSuccess: () -> Unit) {
        val currentState = _uiState.value

        // Validaciones básicas
        if (currentState.fullName.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "El nombre completo es requerido")
            return
        }

        if (currentState.phoneNumber.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "El teléfono es requerido")
            return
        }

        // Separar firstName y lastName del fullName
        val nameParts = currentState.fullName.trim().split("\\s+".toRegex())
        val firstName = if (nameParts.isNotEmpty()) nameParts[0] else ""
        val lastName = if (nameParts.size > 1) nameParts.subList(1, nameParts.size).joinToString(" ") else ""

        if (firstName.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "El nombre es requerido")
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

            updateProfileUseCase(firstName, lastName, currentState.phoneNumber)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSaveSuccessful = true
                    )
                    onSuccess()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al actualizar perfil"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}


