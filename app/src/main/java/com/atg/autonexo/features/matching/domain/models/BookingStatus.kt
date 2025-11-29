package com.atg.autonexo.features.matching.domain.models

enum class BookingStatus (val displayName: String) {
    PENDING_SCHEDULE("Pendiente de Programar"),
    SCHEDULED("Programado"),
    IN_PROGRESS("En Progreso"),
    COMPLETED("Completado"),
    PENDING_PICKUP("Pendiente de Recojo"),
    PICKED_UP("Vehiculo Recogido"),
    CANCELLED("Cancelado")
}