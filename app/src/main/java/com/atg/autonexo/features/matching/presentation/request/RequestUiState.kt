package com.atg.autonexo.features.matching.presentation.request
import com.atg.autonexo.features.matching.domain.models.Request

data class RequestUiState(
    val isLoading: Boolean = false,
    val requests: List<Request> = emptyList(),
    val errorMessage: String? = null
)