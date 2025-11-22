package com.atg.autonexo.features.workshop.presentation.registration.basicinfo

data class BasicInfoUiState(
    val name: String = "",
    val shortDescription: String = "",
    val legalName: String = "",
    val ruc: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val workshopId: Long? = null
)

