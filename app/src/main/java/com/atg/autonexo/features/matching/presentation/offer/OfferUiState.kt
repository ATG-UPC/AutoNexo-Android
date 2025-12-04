package com.atg.autonexo.features.matching.presentation.offer

import java.time.LocalDateTime

data class OfferUiState(
    // Id de la request objetivo (se puede inyectar cuando se abre el modal)
    val serviceRequestId: Long? = null,

    // Inputs que manejamos en la UI
    val proposedPriceInput: String = "",
    val currency: String = "USD",
    val proposedDate: LocalDateTime? = null,
    val message: String = "",

    // Estados de la UI
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)