package com.atg.autonexo.features.payment.domain.models

enum class PaymentMethod(val displayName: String) {
    CREDIT_CARD("Tarjeta de crédito"),

    /**
     * Debit card payment
     */
    DEBIT_CARD("Tarjeta de débito"),

    /**
     * Bank transfer
     */
    BANK_TRANSFER("Transferencia bancaria"),

    /**
     * Digital wallet (Yape, Plin, etc.)
     */
    DIGITAL_WALLET("Wallet digital")
}