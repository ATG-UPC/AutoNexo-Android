package com.atg.autonexo.features.matchingbooking.presentation.request

object ImageResolver {
    fun resolveNameFromDescription(vehicleDescription: String): String? {
        val text = vehicleDescription.lowercase()
        return when {
            "kia" in text -> "auto1"
            "nissan" in text -> "auto2"
            else -> null
        }
    }
}


