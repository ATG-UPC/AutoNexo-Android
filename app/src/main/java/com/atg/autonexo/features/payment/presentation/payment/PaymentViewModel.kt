package com.atg.autonexo.features.payment.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atg.autonexo.features.payment.domain.models.CreatePaymentRequest
import com.atg.autonexo.features.payment.domain.models.Payment
import com.atg.autonexo.features.payment.domain.models.PaymentMethod
import com.atg.autonexo.features.payment.domain.models.SubscriptionPaymentType
import com.atg.autonexo.features.payment.domain.models.SubscriptionTier
import com.atg.autonexo.features.payment.domain.usecases.CreateSubscriptionUseCase
import com.atg.autonexo.features.payment.domain.usecases.GetMySubscriptionUseCase
import com.atg.autonexo.features.payment.domain.usecases.UpdateSubscriptionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val getMySubscriptionUseCase: GetMySubscriptionUseCase,
    private val updateSubscriptionUseCase: UpdateSubscriptionUseCase,
    private val createSubscriptionUseCase: CreateSubscriptionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    fun loadSubscription() {
        viewModelScope.launch {
            _uiState.value = PaymentUiState(isLoading = true)

            getMySubscriptionUseCase()
                .onSuccess { payment ->
                    _uiState.value = PaymentUiState(
                        payment = payment,
                        isLoading = false
                    )
                }
                .onFailure { exception ->
                    _uiState.value = PaymentUiState(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al obtener subscripción"
                    )
                }
        }
    }

    /**
     * Cambia el tier actual por otro y llama al use case de actualización.
     * Asumo que UpdateSubscriptionUseCase devuelve Result<Payment>
     */
    fun updateSubscriptionTier(newTier: SubscriptionTier) {
        val current = _uiState.value.payment ?: return

        val newExpiresAt = LocalDateTime.now().plusDays(30)

        val paymentToUpdate: Payment = current.copy(
            subscriptionTier = newTier,
            expiresAt = newExpiresAt
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            updateSubscriptionUseCase(paymentToUpdate)
                .onSuccess { updatedPayment ->
                    _uiState.value = PaymentUiState(
                        payment = updatedPayment,
                        isLoading = false
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al actualizar suscripción"
                    )
                }
        }
    }

    fun defaultSubscription(workshopId: Long){
        viewModelScope.launch {
            // Si YA hay payment, no hacemos nada
            if (_uiState.value.payment != null) return@launch

            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val request = CreatePaymentRequest(
                workshopId = workshopId,
                subscriptionTier = SubscriptionTier.FREE,
                paymentMethod = PaymentMethod.CREDIT_CARD,
                paymentType = SubscriptionPaymentType.NEW_SUBSCRIPTION,
                description = "Suscripción inicial Free"
            )

            createSubscriptionUseCase(request)
                .onSuccess { payment ->
                    _uiState.value = PaymentUiState(
                        payment = payment,
                        isLoading = false,
                        errorMessage = null
                    )
                }
                .onFailure { e ->
                    _uiState.value = PaymentUiState(
                        payment = null,
                        isLoading = false,
                        errorMessage = e.message ?: "Error al crear suscripción por defecto"
                    )
                }
        }
    }
}
