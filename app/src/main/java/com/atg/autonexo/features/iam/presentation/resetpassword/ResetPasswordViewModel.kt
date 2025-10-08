package com.atg.autonexo.features.iam.presentation.resetpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    // TODO: Inyectar repositorio cuando esté listo
    // private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    fun updateNewPassword(password: String) {
        _uiState.value = _uiState.value.copy(
            newPassword = password,
            errorMessage = null,
            newPasswordError = null,
            repeatPasswordError = null
        )
    }

    fun updateRepeatPassword(password: String) {
        _uiState.value = _uiState.value.copy(
            repeatPassword = password,
            errorMessage = null,
            repeatPasswordError = null
        )
    }

    private fun validateNewPassword(password: String): String? {
        return when {
            password.isBlank() -> "La nueva contraseña es requerida"
            password.length < 8 -> "La contraseña debe tener al menos 8 caracteres"
            !password.any { it.isDigit() } -> "La contraseña debe contener al menos 1 dígito"
            !password.any { it.isLetter() } -> "La contraseña debe contener al menos 1 letra"
            else -> null
        }
    }

    private fun validateRepeatPassword(newPassword: String, repeatPassword: String): String? {
        return when {
            repeatPassword.isBlank() -> "Debe repetir la contraseña"
            newPassword != repeatPassword -> "Las contraseñas no coinciden"
            else -> null
        }
    }

    fun toggleNewPasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isNewPasswordVisible = !_uiState.value.isNewPasswordVisible
        )
    }

    fun toggleRepeatPasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isRepeatPasswordVisible = !_uiState.value.isRepeatPasswordVisible
        )
    }

    fun resetPassword() {
        // Validar todos los campos
        val newPasswordError = validateNewPassword(_uiState.value.newPassword)
        val repeatPasswordError = validateRepeatPassword(_uiState.value.newPassword, _uiState.value.repeatPassword)

        if (newPasswordError != null || repeatPasswordError != null) {
            _uiState.value = _uiState.value.copy(
                newPasswordError = newPasswordError,
                repeatPasswordError = repeatPasswordError
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                newPasswordError = null,
                repeatPasswordError = null,
                errorMessage = null
            )

            try {
                // TODO: Implementar lógica de reset de contraseña cuando el repositorio esté listo
                // val result = authRepository.resetPassword(_uiState.value.newPassword)

                // Simulación temporal
                kotlinx.coroutines.delay(2000)

                // if (result.isSuccess) {
                //     _uiState.value = _uiState.value.copy(
                //         isLoading = false,
                //         isSuccess = true
                //     )
                // } else {
                //     _uiState.value = _uiState.value.copy(
                //         isLoading = false,
                //         errorMessage = result.errorMessage
                //     )
                // }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error desconocido"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
