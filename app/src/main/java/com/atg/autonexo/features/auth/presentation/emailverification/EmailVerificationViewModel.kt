package com.atg.autonexo.features.auth.presentation.emailverification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.features.auth.domain.usecases.ResendVerificationUseCase
import com.atg.autonexo.features.auth.domain.usecases.VerifyEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailVerificationViewModel @Inject constructor(
    private val verifyEmailUseCase: VerifyEmailUseCase,
    private val resendVerificationUseCase: ResendVerificationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmailVerificationUiState())
    val uiState: StateFlow<EmailVerificationUiState> = _uiState.asStateFlow()

    private var userEmail: String = ""

    fun setEmail(email: String) {
        userEmail = email
    }

    fun updateToken(token: String) {
        _uiState.value = _uiState.value.copy(token = token, errorMessage = null)
    }

    fun verifyEmail(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        
        if (currentState.token.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "Por favor ingresa el token de verificación")
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, errorMessage = null)
            
            verifyEmailUseCase(currentState.token)
                .onSuccess { message ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isVerificationSuccessful = true,
                        message = message
                    )
                    onSuccess()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al verificar email"
                    )
                }
        }
    }

    fun resendVerification(email: String? = null) {
        val emailToUse = email ?: userEmail
        if (emailToUse.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "No se puede reenviar el código. Email no disponible."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, message = null)
            
            resendVerificationUseCase(emailToUse)
                .onSuccess { message ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = "Código de verificación reenviado exitosamente. Revisa tu correo electrónico."
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al reenviar código de verificación"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

