package com.atg.autonexo.features.matching.presentation.offerlist

import com.atg.autonexo.features.matching.domain.models.Offer

data class OfferListUiState(
    // Id de la offer selected
    val offerId: Long? = null,

    // Lista de ofertas a cargar
    val offers: List<Offer> = emptyList(),

    // Estados de la UI
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
