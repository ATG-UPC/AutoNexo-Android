package com.atg.autonexo.features.profile.presentation.newpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.features.profile.domain.usecases.ChangePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class NewPasswordViewModel @Inject constructor(
    private val changePasswordUseCase: ChangePasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewPasswordUiState())
    val uiState: StateFlow<NewPasswordUiState> = _uiState.asStateFlow()

    fun updateCurrentPassword(password: String) {
        _uiState.value = _uiState.value.copy(currentPassword = password, errorMessage = null)
    }

    fun updateNewPassword(password: String) {
        _uiState.value = _uiState.value.copy(newPassword = password, errorMessage = null)
    }

    fun updateRepeatPassword(password: String) {
        _uiState.value = _uiState.value.copy(repeatPassword = password, errorMessage = null)
    }

    fun changePassword(onSuccess: () -> Unit) {
        val currentState = _uiState.value

        // Validaciones
        if (currentState.currentPassword.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "La contraseña actual es requerida")
            return
        }

        if (currentState.newPassword.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "La nueva contraseña es requerida")
            return
        }

        if (currentState.newPassword.length < 6) {
            _uiState.value = currentState.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres")
            return
        }

        if (currentState.repeatPassword.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "Por favor repite la contraseña")
            return
        }

        if (currentState.newPassword != currentState.repeatPassword) {
            _uiState.value = currentState.copy(errorMessage = "Las contraseñas no coinciden")
            return
        }

        if (currentState.currentPassword == currentState.newPassword) {
            _uiState.value = currentState.copy(errorMessage = "La nueva contraseña debe ser diferente a la actual")
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

            changePasswordUseCase(currentState.currentPassword, currentState.newPassword)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isPasswordChanged = true
                    )
                    onSuccess()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al cambiar contraseña"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}


