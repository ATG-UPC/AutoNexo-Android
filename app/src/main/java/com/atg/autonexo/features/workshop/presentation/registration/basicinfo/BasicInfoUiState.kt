package com.atg.autonexo.features.workshop.presentation.registration.basicinfo

import com.atg.autonexo.features.workshop.domain.models.Workshop


data class BasicInfoUiState(
    // Original
    val ogName: String = "",
    val ogShortDescription: String = "",
    val ogLegalName: String = "",
    val ogRuc: String = "",


    // Other
    val name: String = "",
    val shortDescription: String = "",
    val legalName: String = "",
    val ruc: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val workshopId: Long? = null,

    val workshop: Workshop? = null
)

