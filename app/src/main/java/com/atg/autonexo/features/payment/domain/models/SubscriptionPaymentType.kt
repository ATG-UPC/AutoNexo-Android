package com.atg.autonexo.features.payment.domain.models

enum class SubscriptionPaymentType(val displayName: String) {
    NEW_SUBSCRIPTION("Suscribirse"),

    /**
     * Payment for subscription renewal
     */
    RENEWAL("Renovar Suscripción"),

    /**
     * Payment for upgrading subscription tier
     */
    UPGRADE("Mejorar Suscripción"),

    /**
     * Payment for downgrading subscription tier
     */
    DOWNGRADE("Cancelar Suscripción")
}