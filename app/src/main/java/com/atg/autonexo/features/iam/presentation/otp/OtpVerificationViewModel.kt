package com.atg.autonexo.features.iam.presentation.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class OtpVerificationViewModel @Inject constructor(
    // TODO: Inyectar repositorio cuando esté listo
    // private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OtpVerificationUiState())
    val uiState: StateFlow<OtpVerificationUiState> = _uiState.asStateFlow()

    fun updateOtp(otp: String) {
        // Solo permitir dígitos y máximo 4 caracteres
        val cleanOtp = otp.filter { it.isDigit() }.take(4)
        
        _uiState.value = _uiState.value.copy(
            otp = cleanOtp,
            errorMessage = null
        )
    }

    fun verifyOtp() {
        if (!_uiState.value.isFormValid) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Por favor ingrese un OTP válido de 4 dígitos"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                // TODO: Implementar lógica de verificación de OTP cuando el repositorio esté listo
                // val result = authRepository.verifyOtp(_uiState.value.otp)

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

    fun resendOtp() {
        if (!_uiState.value.canResend) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                canResend = false,
                resendCooldown = 60 // 60 segundos de cooldown
            )

            try {
                // TODO: Implementar lógica de reenvío de OTP cuando el repositorio esté listo
                // val result = authRepository.resendOtp()

                // Simulación temporal
                kotlinx.coroutines.delay(2000)

                // if (result.isSuccess) {
                //     // Iniciar cooldown
                //     startResendCooldown()
                //     _uiState.value = _uiState.value.copy(
                //         isLoading = false,
                //         errorMessage = "OTP reenviado exitosamente"
                //     )
                // } else {
                //     _uiState.value = _uiState.value.copy(
                //         isLoading = false,
                //         errorMessage = result.errorMessage,
                //         canResend = true
                //     )
                // }

                startResendCooldown()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "OTP reenviado exitosamente"
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error al reenviar OTP",
                    canResend = true
                )
            }
        }
    }

    private fun startResendCooldown() {
        viewModelScope.launch {
            var cooldown = _uiState.value.resendCooldown
            while (cooldown > 0) {
                kotlinx.coroutines.delay(1000) // Esperar 1 segundo
                cooldown--
                _uiState.value = _uiState.value.copy(
                    resendCooldown = cooldown
                )
            }
            _uiState.value = _uiState.value.copy(
                canResend = true,
                resendCooldown = 0
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
