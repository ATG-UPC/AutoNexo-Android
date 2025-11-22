package com.atg.autonexo.features.workshop.presentation.registration.location

import com.google.android.gms.maps.model.LatLng

data class LocationUiState(
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val zip: String = "",
    val country: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val selectedLocation: LatLng? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

