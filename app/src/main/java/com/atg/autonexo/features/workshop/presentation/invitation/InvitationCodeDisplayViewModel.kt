package com.atg.autonexo.features.workshop.presentation.invitation

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
class InvitationCodeDisplayViewModel @Inject constructor(
    private val createInvitationUseCase: CreateInvitationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(InvitationCodeDisplayUiState())
    val uiState: StateFlow<InvitationCodeDisplayUiState> = _uiState.asStateFlow()

    fun createInvitationForWorkshop(workshopName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val request = CreateInvitationRequest(
                email = null, // Código genérico
                message = "Código de invitación para $workshopName",
                validityDays = 365
            )

            createInvitationUseCase(request)
                .onSuccess { invitation ->
                    _uiState.value = _uiState.value.copy(
                        invitationCode = invitation.invitationCode,
                        isLoading = false,
                        errorMessage = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al crear código de invitación"
                    )
                }
        }
    }
}

