package com.atg.autonexo.features.home.presentation.home

import com.atg.autonexo.features.workshop.domain.models.Workshop

data class HomeUiState(
    val userEmail: String? = null,
    val userName: String? = null,
    val workshop: Workshop? = null,
    val hasWorkshop: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

