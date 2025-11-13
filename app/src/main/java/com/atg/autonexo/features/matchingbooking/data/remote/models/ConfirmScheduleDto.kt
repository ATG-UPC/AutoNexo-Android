package com.atg.autonexo.features.matchingbooking.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * Request DTO para confirmar horario
 * Endpoint: POST /api/service-bookings/{id}/confirm-schedule
 * Coincide con ConfirmScheduleResource del backend
 */
data class ConfirmScheduleDto(
    @SerializedName("scheduledDate")
    val scheduledDate: String
)

