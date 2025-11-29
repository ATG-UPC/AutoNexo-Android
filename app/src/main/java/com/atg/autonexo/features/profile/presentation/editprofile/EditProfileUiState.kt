package com.atg.autonexo.features.profile.presentation.editprofile

data class EditProfileUiState(
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSaveSuccessful: Boolean = false
)


