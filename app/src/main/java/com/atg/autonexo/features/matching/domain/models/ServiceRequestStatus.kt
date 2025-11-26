package com.atg.autonexo.features.matching.domain.models

enum class ServiceRequestStatus(val displayName: String) {
    PENDING("Pendiente"),      // Waiting for offers
    CANCELLED("Cancelado"),    // Cancelled by the user
    COMPLETED("Completado"),    // Converted to ServiceBooking and completed
    REJECTED("Rechazado")      // Rejected by a workshop (only affects visibility for that workshop)
}