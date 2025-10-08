package com.atg.autonexo.features.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null
)

sealed class LoginEvent {
    object LoginSuccess : LoginEvent()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    // TODO: Inyectar repositorio cuando esté listo
    // private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    private val _events = MutableSharedFlow<LoginEvent>()
    val events: SharedFlow<LoginEvent> = _events.asSharedFlow()
    
    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            errorMessage = null,
            emailError = null
        )
    }
    
    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            errorMessage = null,
            passwordError = null
        )
    }
    
    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "El email es requerido"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email inválido"
            else -> null
        }
    }
    
    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "La contraseña es requerida"
            password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            else -> null
        }
    }
    
    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )
    }
    
    fun login() {
        // Validar campos
        val emailError = validateEmail(_uiState.value.email)
        val passwordError = validatePassword(_uiState.value.password)
        
        if (emailError != null || passwordError != null) {
            _uiState.value = _uiState.value.copy(
                emailError = emailError,
                passwordError = passwordError
            )
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                emailError = null,
                passwordError = null
            )
            
            try {
                // TODO: Implementar lógica de login cuando el repositorio esté listo
                // val result = authRepository.login(_uiState.value.email, _uiState.value.password)
                
                // Simulación temporal
                kotlinx.coroutines.delay(1000)
                
                // Simulamos login exitoso
                _uiState.value = _uiState.value.copy(isLoading = false)
                _events.emit(LoginEvent.LoginSuccess)
                
                // TODO: Cuando se implemente el repositorio real, usar esto:
                // if (result.isSuccess) {
                //     _uiState.value = _uiState.value.copy(isLoading = false)
                //     _events.emit(LoginEvent.LoginSuccess)
                // } else {
                //     _uiState.value = _uiState.value.copy(
                //         isLoading = false,
                //         errorMessage = result.errorMessage
                //     )
                // }
                
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