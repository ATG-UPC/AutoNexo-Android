package com.atg.autonexo.features.home.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.features.home.domain.usecases.GetHomeInfoUseCase
import com.atg.autonexo.features.home.domain.usecases.LogoutUseCase
import com.atg.autonexo.features.payment.domain.models.Payment
import com.atg.autonexo.features.payment.domain.usecases.CreateSubscriptionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeInfoUseCase: GetHomeInfoUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeInfo()
    }

    fun loadHomeInfo() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            getHomeInfoUseCase()
                .onSuccess { homeInfo ->
                    _uiState.value = _uiState.value.copy(
                        userEmail = homeInfo.userEmail,
                        workshop = homeInfo.workshop,
                        hasWorkshop = homeInfo.workshop != null,
                        isLoading = false
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        hasWorkshop = false,
                        isLoading = false,
                        errorMessage = exception.message
                    )
                }
        }
    }

    fun refresh() {
        loadHomeInfo()
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            logoutUseCase()
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onLogoutSuccess()
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al cerrar sesión"
                    )
                }
        }
    }

}

