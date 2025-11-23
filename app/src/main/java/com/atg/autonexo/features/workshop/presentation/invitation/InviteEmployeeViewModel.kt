package com.atg.autonexo.features.workshop.presentation.invitation

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.features.workshop.domain.models.CreateInvitationRequest
import com.atg.autonexo.features.workshop.domain.usecases.CreateInvitationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InviteEmployeeViewModel @Inject constructor(
    private val createInvitationUseCase: CreateInvitationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(InviteEmployeeUiState())
    val uiState: StateFlow<InviteEmployeeUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }

    fun updateMessage(message: String) {
        _uiState.value = _uiState.value.copy(message = message, errorMessage = null)
    }

    fun updateValidityDays(days: Int) {
        _uiState.value = _uiState.value.copy(validityDays = days, errorMessage = null)
    }

    fun createInvitation() {
        val currentState = _uiState.value

        if (currentState.email.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "El email del empleado es obligatorio")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches()) {
            _uiState.value = currentState.copy(errorMessage = "Formato de email inválido")
            return
        }

        if (currentState.validityDays < 1) {
            _uiState.value = currentState.copy(errorMessage = "La validez debe ser al menos de 1 día")
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

            val request = CreateInvitationRequest(
                email = currentState.email.trim(),
                message = currentState.message.trim().takeIf { it.isNotBlank() },
                validityDays = currentState.validityDays
            )

            createInvitationUseCase(request)
                .onSuccess { invitation ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        invitation = invitation,
                        errorMessage = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error al generar la invitación"
                    )
                }
        }
    }

    fun resetInvitation() {
        _uiState.value = _uiState.value.copy(invitation = null)
    }
}

