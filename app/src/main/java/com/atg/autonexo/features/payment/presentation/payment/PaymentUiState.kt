package com.atg.autonexo.features.payment.presentation.payment

import com.atg.autonexo.features.payment.domain.models.Payment
import com.atg.autonexo.features.workshop.domain.models.Workshop

data class PaymentUiState (
    val payment: Payment? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val workshop: Workshop? = null
)