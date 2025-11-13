package com.atg.autonexo.features.matchingbooking.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * Request DTO para proponer cambio de horario
 * Endpoint: POST /api/service-bookings/{id}/propose-change
 * Coincide con ProposeScheduleChangeResource del backend
 */
data class ProposeScheduleChangeDto(
    @SerializedName("newScheduledDate")
    val newScheduledDate: String
)

