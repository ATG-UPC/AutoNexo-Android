package com.atg.autonexo.features.workshop.presentation.invitation

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

    fun updateCode(code: String) {
        _uiState.value = _uiState.value.copy(
            code = code.uppercase().take(8),
            errorMessage = null
        )
    }

    fun acceptInvitation(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        
        if (currentState.code.length != 8) {
            _uiState.value = currentState.copy(
                errorMessage = "El código debe tener 8 caracteres"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

            // Obtener email del usuario actual
            getCurrentUserUseCase()
                .onSuccess { user ->
                    val email = user.email
                    
                    if (email.isBlank()) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "No se pudo obtener el email del usuario"
                        )
                        return@launch
                    }

                    val request = AcceptInvitationRequest(
                        invitationCode = currentState.code,
                        email = email
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
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al obtener información del usuario"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

