package com.atg.autonexo.features.auth.presentation.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    // TODO: Inyectar repositorio cuando esté listo
    // private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun updatePhone(phone: String) {
        _uiState.value = _uiState.value.copy(
            phone = phone,
            errorMessage = null,
            phoneError = null
        )
    }

    private fun validatePhone(phone: String): String? {
        // Limpiar espacios y guiones para validar solo dígitos
        val cleanPhone = phone.replace(" ", "").replace("-", "")
        
        return when {
            cleanPhone.isBlank() -> "El número de teléfono es requerido"
            cleanPhone.length < 9 -> "El teléfono debe tener al menos 9 dígitos"
            cleanPhone.length > 15 -> "El teléfono no puede tener más de 15 dígitos"
            !cleanPhone.all { it.isDigit() } -> "El teléfono solo puede contener dígitos"
            else -> null
        }
    }

    fun submitPhone() {
        val phoneError = validatePhone(_uiState.value.phone)

        if (phoneError != null) {
            _uiState.value = _uiState.value.copy(
                phoneError = phoneError
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                phoneError = null,
                errorMessage = null
            )

            try {
                // TODO: Implementar lógica de envío de OTP cuando el repositorio esté listo
                // val result = authRepository.sendOtp(_uiState.value.phone)

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
