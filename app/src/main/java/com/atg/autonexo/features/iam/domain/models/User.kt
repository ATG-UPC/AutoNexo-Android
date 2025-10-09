package com.atg.autonexo.features.iam.domain.models

/**
 * Modelo de dominio simplificado para Usuario
 * Representa la entidad User en la capa de dominio
 */
data class User(
    val id: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val isVerified: Boolean = false,
    val active: Boolean = true,
    val roles: List<String> = emptyList(),
    val workshopId: Long? = null
) {
    val fullName: String
        get() = "$firstName $lastName"
    
    fun isWorkshopManager(): Boolean = roles.contains("WORKSHOP_MANAGER")
    
    fun isCarOwner(): Boolean = roles.contains("CAR_OWNER")
    
    fun isWorkshopEmployee(): Boolean = roles.contains("WORKSHOP_EMPLOYEE")
}
