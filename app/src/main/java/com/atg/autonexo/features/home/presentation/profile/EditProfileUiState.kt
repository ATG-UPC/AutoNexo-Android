package com.atg.autonexo.features.home.presentation.profile

data class EditProfileUiState(
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val fullNameError: String? = null,
    val emailError: String? = null,
    val phoneNumberError: String? = null,
    val isLoading: Boolean = false,
    val isSaveEnabled: Boolean = false
)

