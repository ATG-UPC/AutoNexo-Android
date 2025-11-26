package com.atg.autonexo.features.matching.domain.models

enum class OfferStatus(val displayName: String) {
    PENDING("Pendiente"),   // Waiting for response
    ACCEPTED("Aceptada"),  // Accepted by the user (converts to ServiceBooking)
    REJECTED("Rechazada"),  // Rejected by the user
    EXPIRED("Expirada"),   // Automatically expired after 3 days
    WITHDRAWN("Retirada")  // Withdrawn by the workshop
}