package com.atg.autonexo.features.vehiclemaintenance.domain.model

data class LicensePlate(
    val value: String
) {
    init {
        require(value.isNotBlank()) { "License plate cannot be blank" }
        require(validatePlate()) { "Invalid license plate format" }
    }

    private fun validatePlate(): Boolean {
        // Validación básica: alfanumérico con guiones opcionales
        return value.matches(Regex("^[A-Z0-9\\-]{3,10}$"))
    }

    override fun toString(): String = value
}

