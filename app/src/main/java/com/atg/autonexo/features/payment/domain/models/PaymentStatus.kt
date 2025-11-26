package com.atg.autonexo.features.payment.domain.models

enum class PaymentStatus(val displayName: String) {
    PENDING("Pendiente"),

    /**
     * Payment completed successfully
     */
    COMPLETED("Completado"),

    /**
     * Payment failed
     */
    FAILED("Fallido"),

    /**
     * Payment was refunded
     */
    REFUNDED("Reembolsado"),

    /**
     * Payment was cancelled before processing
     */
    CANCELLED("Cancelado"),
    ACTIVE("Activo")
}