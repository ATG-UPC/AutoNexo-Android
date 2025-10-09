package com.atg.autonexo.features.iam.presentation.profile

data class EditProfileUiState(
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val fullNameError: String? = null,
    val emailError: String? = null,
    val phoneNumberError: String? = null,
    val isLoading: Boolean = false,
    val isSaveEnabled: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val showErrorDialog: Boolean = false,
    val errorMessage: String = ""
)

