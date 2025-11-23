package com.atg.autonexo.features.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.features.auth.domain.models.Role
import com.atg.autonexo.features.auth.domain.models.SignUpRequest
import com.atg.autonexo.features.auth.domain.usecases.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword, errorMessage = null)
    }

    fun updateFirstName(firstName: String) {
        _uiState.value = _uiState.value.copy(firstName = firstName, errorMessage = null)
    }

    fun updateLastName(lastName: String) {
        _uiState.value = _uiState.value.copy(lastName = lastName, errorMessage = null)
    }

    fun updatePhoneNumber(phoneNumber: String) {
        _uiState.value = _uiState.value.copy(phoneNumber = phoneNumber, errorMessage = null)
    }

    fun updateRequestedRole(role: Role) {
        _uiState.value = _uiState.value.copy(requestedRole = role, errorMessage = null)
    }

    fun updateInvitationCode(code: String) {
        _uiState.value = _uiState.value.copy(invitationCode = code, errorMessage = null)
    }

    fun signUp(onSuccess: (email: String) -> Unit) {
        val currentState = _uiState.value
        
        // Validaciones de campos vacíos
        if (currentState.email.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "El email es requerido")
            return
        }
        
        if (currentState.firstName.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "El nombre es requerido")
            return
        }
        
        if (currentState.lastName.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "El apellido es requerido")
            return
        }
        
        if (currentState.phoneNumber.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "El teléfono es requerido")
            return
        }
        
        if (currentState.password.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "La contraseña es requerida")
            return
        }

        // Validación de formato de email
        val emailPattern = android.util.Patterns.EMAIL_ADDRESS
        if (!emailPattern.matcher(currentState.email.trim()).matches()) {
            _uiState.value = currentState.copy(errorMessage = "El formato del email no es válido")
            return
        }

        // Validación de longitud de contraseña
        if (currentState.password.length < 6) {
            _uiState.value = currentState.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres")
            return
        }

        // Validación de coincidencia de contraseñas
        if (currentState.password != currentState.confirmPassword) {
            _uiState.value = currentState.copy(errorMessage = "Las contraseñas no coinciden")
            return
        }

        // Validación de código de invitación para empleados
        if (currentState.requestedRole == Role.WORKSHOP_EMPLOYEE && currentState.invitationCode.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "El código de invitación es requerido para empleados")
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, errorMessage = null)
            
            val request = SignUpRequest(
                email = currentState.email.trim(),
                password = currentState.password,
                firstName = currentState.firstName.trim(),
                lastName = currentState.lastName.trim(),
                phoneNumber = currentState.phoneNumber.trim(),
                requestedRole = currentState.requestedRole,
                invitationCode = if (currentState.invitationCode.isBlank()) null else currentState.invitationCode.trim()
            )
            
            val emailTrimmed = currentState.email.trim()
            
            signUpUseCase(request)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRegisterSuccessful = true
                    )
                    onSuccess(emailTrimmed) // Pasar el email al callback
                }
                .onFailure { exception ->
                    val errorMessage = exception.message ?: "Error desconocido al registrar usuario"
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

