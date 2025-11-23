package com.atg.autonexo.features.workshop.presentation.invitation

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.features.auth.domain.usecases.GetCurrentUserUseCase
import com.atg.autonexo.features.workshop.domain.models.AcceptInvitationRequest
import com.atg.autonexo.features.workshop.domain.usecases.AcceptInvitationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AcceptInvitationViewModel @Inject constructor(
    private val acceptInvitationUseCase: AcceptInvitationUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AcceptInvitationUiState())
    val uiState: StateFlow<AcceptInvitationUiState> = _uiState.asStateFlow()

    init {
        prefillEmail()
    }

    fun updateCode(code: String) {
        _uiState.value = _uiState.value.copy(
            code = code.uppercase().take(8),
            errorMessage = null
        )
    }

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }

    private fun prefillEmail() {
        viewModelScope.launch {
            getCurrentUserUseCase()
                .onSuccess { user ->
                    if (user.email.isNotBlank()) {
                        _uiState.value = _uiState.value.copy(email = user.email)
                    }
                }
        }
    }

    fun acceptInvitation(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        
        if (currentState.code.length != 8) {
            _uiState.value = currentState.copy(
                errorMessage = "El código debe tener 8 caracteres"
            )
            return
        }

        if (currentState.email.isBlank()) {
            _uiState.value = currentState.copy(
                errorMessage = "El email es requerido"
            )
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches()) {
            _uiState.value = currentState.copy(
                errorMessage = "Formato de email inválido"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

            val request = AcceptInvitationRequest(
                invitationCode = currentState.code,
                email = currentState.email.trim()
            )

            acceptInvitationUseCase(request)
                .onSuccess { message ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = message
                    )
                    onSuccess()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al aceptar invitación"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

