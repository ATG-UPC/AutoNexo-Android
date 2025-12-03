package com.atg.autonexo.features.matching.presentation.requests

import com.atg.autonexo.features.matching.domain.models.Request

data class RequestsUiState(
    val isLoading: Boolean = false,
    val requests: List<Request> = emptyList(),
    val errorMessage: String? = null
)