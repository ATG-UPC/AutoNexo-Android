package com.atg.autonexo.features.iam.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class NewPasswordEvent {
    object SaveSuccess : NewPasswordEvent()
}

@HiltViewModel
class NewPasswordViewModel @Inject constructor(
    // TODO: Inyectar repositorios
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewPasswordUiState())
    val uiState: StateFlow<NewPasswordUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<NewPasswordEvent>()
    val events: SharedFlow<NewPasswordEvent> = _events.asSharedFlow()

    fun updateNewPassword(value: String) {
        _uiState.update { it.copy(newPassword = value, newPasswordError = null) }
        validateAll()
    }

    fun updateRepeatPassword(value: String) {
        _uiState.update { it.copy(repeatPassword = value, repeatPasswordError = null) }
        validateAll()
    }

    fun toggleNewPasswordVisibility() {
        _uiState.update { it.copy(isNewPasswordVisible = !it.isNewPasswordVisible) }
    }

    fun toggleRepeatPasswordVisibility() {
        _uiState.update { it.copy(isRepeatPasswordVisible = !it.isRepeatPasswordVisible) }
    }

    private fun validateAll() {
        val state = _uiState.value
        
        val newPasswordError = when {
            state.newPassword.length < 8 -> "Mínimo 8 caracteres"
            !state.newPassword.contains(Regex("[a-zA-Z]")) -> "Debe contener al menos una letra"
            !state.newPassword.contains(Regex("\\d")) -> "Debe contener al menos un dígito"
            else -> null
        }

        val repeatPasswordError = when {
            state.repeatPassword.isEmpty() -> null
            state.newPassword != state.repeatPassword -> "Las contraseñas no coinciden"
            else -> null
        }

        _uiState.update {
            it.copy(
                newPasswordError = newPasswordError,
                repeatPasswordError = repeatPasswordError,
                isSaveEnabled = newPasswordError == null && 
                               repeatPasswordError == null && 
                               state.newPassword.isNotEmpty() && 
                               state.repeatPassword.isNotEmpty()
            )
        }
    }

    fun savePassword() {
        if (!_uiState.value.isSaveEnabled) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // TODO: Guardar en repositorio
            kotlinx.coroutines.delay(1000)
            
            _uiState.update { it.copy(isLoading = false) }
            _events.emit(NewPasswordEvent.SaveSuccess)
        }
    }
}

