package com.atg.autonexo.features.home.presentation.profile

import com.atg.autonexo.features.home.presentation.profile.models.UserProfileUi

data class ProfileUiState(
    val isLoading: Boolean = false,
    val userProfile: UserProfileUi? = null,
    val error: String? = null
)

