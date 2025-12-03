package com.atg.autonexo.features.matching.presentation.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.features.matching.domain.usecases.GetRequestsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class RequestsViewModel @Inject constructor(
    private val getRequestsUseCase: GetRequestsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RequestsUiState())
    val uiState: StateFlow<RequestsUiState> = _uiState.asStateFlow()

    init {
        loadServiceRequests()
    }

    fun loadServiceRequests() {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            getRequestsUseCase()
                .onSuccess { requests ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        requests = requests,
                        errorMessage = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al obtener solicitudes"
                    )
                }
        }
    }
}