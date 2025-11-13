package com.atg.autonexo.features.workshop.presentation.models

data class WorkshopUi(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val logoUrl: String?,
    val rating: Float,
    val legalName: String?,
    val ruc: String?,
    val address: String,
    val district: String,
    val city: String,
    val description: String,
    val services: List<String>,
    val mechanics: List<MechanicUi>,
    val isOwner: Boolean = false,
    val workshopCode: String? = null
)

data class MechanicUi(
    val id: String,
    val name: String,
    val role: String,
    val avatarUrl: String? = null
)
