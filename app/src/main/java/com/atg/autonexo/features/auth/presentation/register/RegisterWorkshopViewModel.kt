package com.atg.autonexo.features.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterWorkshopUiState(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val workshopCode: String = "",
    val termsAccepted: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isRepeatPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val fullNameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val passwordError: String? = null,
    val repeatPasswordError: String? = null
) {
    val isFormValid: Boolean
        get() = fullName.isNotBlank() &&
                email.isNotBlank() &&
                phone.isNotBlank() &&
                password.isNotBlank() &&
                repeatPassword.isNotBlank() &&
                password == repeatPassword &&
                password.length >= 8 &&
                phone.length >= 9 &&
                termsAccepted &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

@HiltViewModel
class RegisterWorkshopViewModel @Inject constructor(
    // TODO: Inyectar repositorio cuando esté listo
    // private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(RegisterWorkshopUiState())
    val uiState: StateFlow<RegisterWorkshopUiState> = _uiState.asStateFlow()
    
    fun updateFullName(name: String) {
        _uiState.value = _uiState.value.copy(
            fullName = name,
            errorMessage = null,
            fullNameError = null
        )
    }
    
    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            errorMessage = null,
            emailError = null
        )
    }
    
    fun updatePhone(phone: String) {
        _uiState.value = _uiState.value.copy(
            phone = phone,
            errorMessage = null,
            phoneError = null
        )
    }
    
    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            errorMessage = null,
            passwordError = null,
            repeatPasswordError = null
        )
    }
    
    fun updateRepeatPassword(repeatPassword: String) {
        _uiState.value = _uiState.value.copy(
            repeatPassword = repeatPassword,
            errorMessage = null,
            repeatPasswordError = null
        )
    }
    
    fun updateWorkshopCode(workshopCode: String) {
        _uiState.value = _uiState.value.copy(
            workshopCode = workshopCode,
            errorMessage = null
        )
    }
    
    fun updateTermsAccepted(accepted: Boolean) {
        _uiState.value = _uiState.value.copy(
            termsAccepted = accepted,
            errorMessage = null
        )
    }
    
    private fun validateFullName(name: String): String? {
        return when {
            name.isBlank() -> "El nombre completo es requerido"
            name.length < 3 -> "El nombre debe tener al menos 3 caracteres"
            else -> null
        }
    }
    
    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "El email es requerido"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Email inválido"
            else -> null
        }
    }
    
    private fun validatePhone(phone: String): String? {
        return when {
            phone.isBlank() -> "El teléfono es requerido"
            phone.length < 9 -> "El teléfono debe tener al menos 9 dígitos"
            phone.length > 15 -> "El teléfono no puede tener más de 15 dígitos"
            !phone.all { it.isDigit() || it == '-' } -> "El teléfono solo puede contener dígitos y guiones"
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
    
    private fun validateRepeatPassword(password: String, repeatPassword: String): String? {
        return when {
            repeatPassword.isBlank() -> "Debe repetir la contraseña"
            password != repeatPassword -> "Las contraseñas no coinciden"
            else -> null
        }
    }
    
    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isPasswordVisible = !_uiState.value.isPasswordVisible
        )
    }
    
    fun toggleRepeatPasswordVisibility() {
        _uiState.value = _uiState.value.copy(
            isRepeatPasswordVisible = !_uiState.value.isRepeatPasswordVisible
        )
    }
    
    fun showWorkshopCodeInfo() {
        // TODO: Mostrar información sobre el Workshop Code
        // Podría ser un dialog o navegación a una pantalla de ayuda
    }
    
    fun showTermsAndConditions() {
        // TODO: Mostrar términos y condiciones
        // Podría ser un dialog o navegación a una pantalla web
    }
    
    fun proceedToNextStep() {
        // Validar todos los campos
        val fullNameError = validateFullName(_uiState.value.fullName)
        val emailError = validateEmail(_uiState.value.email)
        val phoneError = validatePhone(_uiState.value.phone)
        val passwordError = validatePassword(_uiState.value.password)
        val repeatPasswordError = validateRepeatPassword(_uiState.value.password, _uiState.value.repeatPassword)
        
        if (fullNameError != null || emailError != null || phoneError != null || 
            passwordError != null || repeatPasswordError != null) {
            _uiState.value = _uiState.value.copy(
                fullNameError = fullNameError,
                emailError = emailError,
                phoneError = phoneError,
                passwordError = passwordError,
                repeatPasswordError = repeatPasswordError
            )
            return
        }
        
        if (!_uiState.value.termsAccepted) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Debe aceptar los términos y condiciones"
            )
            return
        }
        
        // TODO: Navegar al siguiente paso del registro
        // Este será el paso 2 del flujo de 3 pasos
        _uiState.value = _uiState.value.copy(
            fullNameError = null,
            emailError = null,
            phoneError = null,
            passwordError = null,
            repeatPasswordError = null
        )
    }
    
    fun registerWorkshop() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // Validaciones
                if (_uiState.value.password != _uiState.value.repeatPassword) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Las contraseñas no coinciden"
                    )
                    return@launch
                }
                
                if (!_uiState.value.termsAccepted) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Debe aceptar los términos y condiciones"
                    )
                    return@launch
                }
                
                // TODO: Implementar lógica de registro cuando el repositorio esté listo
                // val result = authRepository.registerWorkshop(
                //     fullName = _uiState.value.fullName,
                //     email = _uiState.value.email,
                //     phone = _uiState.value.phone,
                //     password = _uiState.value.password,
                //     workshopCode = _uiState.value.workshopCode
                // )
                
                // Simulación temporal
                kotlinx.coroutines.delay(2000)
                
                // if (result.isSuccess) {
                //     // Navegar al siguiente paso
                // } else {
                //     _uiState.value = _uiState.value.copy(
                //         errorMessage = result.errorMessage
                //     )
                // }
                
                _uiState.value = _uiState.value.copy(isLoading = false)
                
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