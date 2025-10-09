package com.atg.autonexo.features.iam.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.features.iam.domain.models.AuthResult
import com.atg.autonexo.features.iam.domain.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val selectedRole: String = "CAR_OWNER",
    val invitationCode: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val phoneNumberError: String? = null,
    val invitationCodeError: String? = null
)

sealed class RegisterEvent {
    data class RegisterSuccess(val message: String) : RegisterEvent()
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()
    
    private val _events = MutableSharedFlow<RegisterEvent>()
    val events: SharedFlow<RegisterEvent> = _events.asSharedFlow()
    
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
    
    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = confirmPassword,
            errorMessage = null,
            confirmPasswordError = null
        )
    }
    
    fun updateFirstName(firstName: String) {
        _uiState.value = _uiState.value.copy(
            firstName = firstName,
            errorMessage = null,
            firstNameError = null
        )
    }
    
    fun updateLastName(lastName: String) {
        _uiState.value = _uiState.value.copy(
            lastName = lastName,
            errorMessage = null,
            lastNameError = null
        )
    }
    
    fun updatePhoneNumber(phoneNumber: String) {
        _uiState.value = _uiState.value.copy(
            phoneNumber = phoneNumber,
            errorMessage = null,
            phoneNumberError = null
        )
    }
    
    fun updateSelectedRole(role: String) {
        _uiState.value = _uiState.value.copy(
            selectedRole = role,
            errorMessage = null
        )
    }
    
    fun updateInvitationCode(code: String) {
        _uiState.value = _uiState.value.copy(
            invitationCode = code,
            errorMessage = null,
            invitationCodeError = null
        )
    }
    
    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )
    }
    
    fun toggleConfirmPasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isConfirmPasswordVisible = !_uiState.value.isConfirmPasswordVisible
        )
    }
    
    fun register() {
        // Validar todos los campos
        val emailError = validateEmail(_uiState.value.email)
        val passwordError = validatePassword(_uiState.value.password)
        val confirmPasswordError = validateConfirmPassword(
            _uiState.value.password, 
            _uiState.value.confirmPassword
        )
        val firstNameError = validateName(_uiState.value.firstName, "First name")
        val lastNameError = validateName(_uiState.value.lastName, "Last name")
        val phoneNumberError = validatePhoneNumber(_uiState.value.phoneNumber)
        val invitationCodeError = if (_uiState.value.selectedRole == "WORKSHOP_EMPLOYEE") {
            validateInvitationCode(_uiState.value.invitationCode)
        } else null
        
        if (emailError != null || passwordError != null || confirmPasswordError != null ||
            firstNameError != null || lastNameError != null || phoneNumberError != null ||
            invitationCodeError != null) {
            _uiState.value = _uiState.value.copy(
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError,
                firstNameError = firstNameError,
                lastNameError = lastNameError,
                phoneNumberError = phoneNumberError,
                invitationCodeError = invitationCodeError
            )
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                emailError = null,
                passwordError = null,
                confirmPasswordError = null,
                firstNameError = null,
                lastNameError = null,
                phoneNumberError = null,
                invitationCodeError = null
            )
            
            val result = authRepository.signUp(
                email = _uiState.value.email,
                password = _uiState.value.password,
                firstName = _uiState.value.firstName,
                lastName = _uiState.value.lastName,
                phoneNumber = _uiState.value.phoneNumber,
                requestedRole = _uiState.value.selectedRole,
                invitationCode = if (_uiState.value.invitationCode.isNotBlank()) {
                    _uiState.value.invitationCode
                } else null
            )
            
            when (result) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    _events.emit(RegisterEvent.RegisterSuccess(result.data))
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is AuthResult.Loading -> {
                    // No debería llegar aquí
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    // ========== Validation Functions ==========
    
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
            password.length < 8 -> "La contraseña debe tener al menos 8 caracteres"
            else -> null
        }
    }
    
    private fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isBlank() -> "Confirma tu contraseña"
            confirmPassword != password -> "Las contraseñas no coinciden"
            else -> null
        }
    }
    
    private fun validateName(name: String, fieldName: String): String? {
        return when {
            name.isBlank() -> "$fieldName es requerido"
            name.length < 2 -> "$fieldName debe tener al menos 2 caracteres"
            name.length > 50 -> "$fieldName no puede tener más de 50 caracteres"
            else -> null
        }
    }
    
    private fun validatePhoneNumber(phoneNumber: String): String? {
        return when {
            phoneNumber.isBlank() -> "El teléfono es requerido"
            phoneNumber.length < 10 -> "El teléfono debe tener al menos 10 dígitos"
            phoneNumber.length > 15 -> "El teléfono no puede tener más de 15 dígitos"
            !phoneNumber.all { it.isDigit() || it == '+' || it == '-' || it == ' ' } -> 
                "Formato de teléfono inválido"
            else -> null
        }
    }
    
    private fun validateInvitationCode(code: String): String? {
        return when {
            code.isBlank() -> "El código de invitación es requerido para empleados"
            else -> null
        }
    }
}




