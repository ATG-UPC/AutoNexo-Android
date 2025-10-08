package com.atg.autonexo.features.home.presentation.profile

import android.util.Patterns
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

sealed class EditProfileEvent {
    object SaveSuccess : EditProfileEvent()
}

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    // TODO: Inyectar repositorios
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<EditProfileEvent>()
    val events: SharedFlow<EditProfileEvent> = _events.asSharedFlow()

    fun loadProfileData(fullName: String, email: String, phoneNumber: String) {
        _uiState.update {
            it.copy(
                fullName = fullName,
                email = email,
                phoneNumber = phoneNumber
            )
        }
        validateAll()
    }

    fun updateFullName(value: String) {
        _uiState.update { it.copy(fullName = value, fullNameError = null) }
        validateAll()
    }

    fun updateEmail(value: String) {
        _uiState.update { it.copy(email = value, emailError = null) }
        validateAll()
    }

    fun updatePhoneNumber(value: String) {
        _uiState.update { it.copy(phoneNumber = value, phoneNumberError = null) }
        validateAll()
    }

    private fun validateAll() {
        val state = _uiState.value
        val fullNameError = if (state.fullName.isBlank()) "El nombre es requerido" else null
        val emailError = if (!Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) 
            "Email inválido" else null
        val phoneError = if (!state.phoneNumber.matches(Regex("^[\\d\\s\\-]+$"))) 
            "Teléfono inválido" else null

        _uiState.update {
            it.copy(
                fullNameError = fullNameError,
                emailError = emailError,
                phoneNumberError = phoneError,
                isSaveEnabled = fullNameError == null && emailError == null && phoneError == null
            )
        }
    }

    fun saveProfile() {
        if (!_uiState.value.isSaveEnabled) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // TODO: Guardar en repositorio
            kotlinx.coroutines.delay(1000)
            
            _uiState.update { it.copy(isLoading = false) }
            _events.emit(EditProfileEvent.SaveSuccess)
        }
    }
}

