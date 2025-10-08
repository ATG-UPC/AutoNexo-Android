package com.atg.autonexo.features.matchingbooking.domain.model

enum class ServiceType {
    INSPECTION,
    OIL_CHANGE,
    TIRES,
    BRAKES,
    BODYWORK,
    OTHER;
    
    fun getDisplayName(): String {
        return when (this) {
            INSPECTION -> "Inspection"
            OIL_CHANGE -> "Oil Change"
            TIRES -> "Tires"
            BRAKES -> "Brakes"
            BODYWORK -> "Bodywork"
            OTHER -> "Other"
        }
    }
}

