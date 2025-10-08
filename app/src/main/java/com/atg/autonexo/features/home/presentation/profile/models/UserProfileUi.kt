package com.atg.autonexo.features.home.presentation.profile.models

data class UserProfileUi(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val role: String = "",
    val rating: Double = 0.0,
    val currentWorkshop: String = "",
    val avatarUrl: String? = null,
    val messageNotifications: Boolean = true,
    val offersNotifications: Boolean = true
)

