package com.atg.autonexo.features.profile.presentation

import com.atg.autonexo.features.profile.domain.models.Profile

data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: Profile? = null,
    val errorMessage: String? = null,
    val messageNotificationsEnabled: Boolean = false,
    val offersNotificationsEnabled: Boolean = false
)

