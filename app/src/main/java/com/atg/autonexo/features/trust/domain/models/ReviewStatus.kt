package com.atg.autonexo.features.trust.domain.models

enum class ReviewStatus(val displayName: String) {
    /**
     * Review window is pending - service not yet completed
     */
    PENDING_WINDOW("Ventana Pendiente"),

    /**
     * Review is available to be submitted - within 14 day window
     */
    AVAILABLE("Disponible"),

    /**
     * Review window has expired - 14 days passed without submission
     */
    EXPIRED("Expirada"),

    /**
     * Review has been submitted
     */
    SUBMITTED("Enviada")
}