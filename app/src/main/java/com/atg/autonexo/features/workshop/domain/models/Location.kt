package com.atg.autonexo.features.workshop.domain.models

data class Location(
    val id: Long,
    val street: String,
    val city: String,
    val state: String,
    val zip: String,
    val country: String,
    val latitude: Double?,
    val longitude: Double?,
    val active: Boolean = true
)

