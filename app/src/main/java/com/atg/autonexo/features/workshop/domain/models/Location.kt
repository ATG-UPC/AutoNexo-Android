package com.atg.autonexo.features.workshop.domain.models

data class Location(
    val id: Long,
    val address: String,
    val district: String,
    val city: String,
    val latitude: Double,
    val longitude: Double,
    val isPrimary: Boolean
)

