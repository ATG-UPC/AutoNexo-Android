package com.atg.autonexo.features.subscription.presentation.plans

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.atg.autonexo.features.subscription.domain.repositories.SubscriptionRepository
import com.atg.autonexo.features.iam.domain.models.AuthResult
import com.atg.autonexo.core.data.UserPreferences

data class SubscribeUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val showSuccessDialog: Boolean = false,
    val showErrorDialog: Boolean = false
)

@HiltViewModel
class SubscribeViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubscribeUiState())
    val uiState: StateFlow<SubscribeUiState> = _uiState.asStateFlow()

    fun subscribe(tier: String, billingCycle: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    showErrorDialog = false,
                    errorMessage = null
                )

                // Obtener workshopId del usuario
                val workshopId = userPreferences.getWorkshopId()
                Log.d("SubscribeVM", "Retrieved workshopId from preferences: $workshopId")
                
                if (workshopId == null) {
                    Log.e("SubscribeVM", "Workshop ID is null - user may not have created a workshop yet")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "No se encontró el taller asociado. Por favor, crea un taller primero.",
                        showErrorDialog = true
                    )
                    return@launch
                }

                // Generar descripción con el ciclo de facturación
                val billingCycleText = if (billingCycle == "Monthly") "Mensual" else "Anual"
                val tierDisplayName = when (tier) {
                    "BASIC" -> "Pro"
                    "PREMIUM" -> "Premium"
                    else -> tier
                }
                val description = "Suscripción $tierDisplayName - $billingCycleText"

                Log.d("SubscribeVM", "Subscribing: workshopId=$workshopId, tier=$tier (display: $tierDisplayName), billing=$billingCycle")

                val result = subscriptionRepository.createSubscriptionPayment(
                    workshopId = workshopId,
                    subscriptionTier = tier,
                    paymentMethod = "CREDIT_CARD",
                    paymentType = "NEW_SUBSCRIPTION",
                    description = description
                )

                when (result) {
                    is AuthResult.Success -> {
                        Log.d("SubscribeVM", "Subscription successful")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            showSuccessDialog = true
                        )
                    }
                    is AuthResult.Error -> {
                        Log.e("SubscribeVM", "Subscription error: ${result.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = result.message,
                            showErrorDialog = true
                        )
                    }
                    else -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Error desconocido al crear suscripción",
                            showErrorDialog = true
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("SubscribeVM", "Exception during subscription", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error: ${e.message}",
                    showErrorDialog = true
                )
            }
        }
    }

    fun dismissSuccessDialog() {
        _uiState.value = _uiState.value.copy(showSuccessDialog = false)
    }

    fun dismissErrorDialog() {
        _uiState.value = _uiState.value.copy(showErrorDialog = false, errorMessage = null)
    }

    fun resetState() {
        _uiState.value = SubscribeUiState()
    }
}
