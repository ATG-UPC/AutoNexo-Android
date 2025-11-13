package com.atg.autonexo.features.matchingbooking.presentation.request

import com.atg.autonexo.features.matchingbooking.presentation.request.models.ServiceRequestUi

data class RequestListUiState(
    val isLoading: Boolean = false,
    val requests: List<ServiceRequestUi> = emptyList(),
    val selectedFilter: RequestFilter = RequestFilter.ALL,
    val errorMessage: String? = null
)

enum class RequestFilter {
    ALL,
    PENDING,
    MATCHING,
    ARCHIVED
}

