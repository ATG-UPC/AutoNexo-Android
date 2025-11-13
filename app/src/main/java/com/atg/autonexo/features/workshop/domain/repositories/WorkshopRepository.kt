package com.atg.autonexo.features.workshop.domain.repositories

import com.atg.autonexo.features.iam.domain.models.AuthResult
import com.atg.autonexo.features.workshop.domain.models.*
import java.io.File

/**
 * Repositorio de dominio para operaciones de Workshop
 */
interface WorkshopRepository {
    
    // ========== Workshop Management ==========
    
    suspend fun createWorkshop(
        name: String,
        description: String,
        contactEmail: String,
        contactPhone: String,
        address: String,
        district: String,
        city: String,
        latitude: Double,
        longitude: Double
    ): AuthResult<Workshop>
    
    suspend fun getMyWorkshop(): AuthResult<Workshop>
    
    suspend fun getWorkshopById(workshopId: Long): AuthResult<Workshop>
    
    suspend fun updateWorkshop(
        name: String?,
        description: String?,
        contactEmail: String?,
        contactPhone: String?
    ): AuthResult<Workshop>
    
    // ========== Location Management ==========
    
    suspend fun addLocation(
        address: String,
        district: String,
        city: String,
        latitude: Double,
        longitude: Double,
        isPrimary: Boolean
    ): AuthResult<Location>
    
    suspend fun getMyWorkshopLocations(): AuthResult<List<Location>>
    
    suspend fun deleteLocation(locationId: Long): AuthResult<String>
    
    // ========== Service Templates ==========
    
    suspend fun addServiceTemplate(
        serviceName: String,
        serviceCategory: String,
        basePrice: Double,
        estimatedDurationMinutes: Int,
        description: String?
    ): AuthResult<ServiceTemplate>
    
    // ========== Capability Tags ==========
    
    suspend fun addCapabilityTag(tag: String): AuthResult<String>
    
    suspend fun updateCapabilityTags(tags: List<String>): AuthResult<String>
    
    // ========== Media Management ==========
    
    suspend fun uploadLogo(imageFile: File): AuthResult<String>
    
    suspend fun addPhoto(imageFile: File): AuthResult<String>
    
    suspend fun deletePhoto(photoIndex: Int): AuthResult<String>
    
    // ========== Subscription ==========
    
    suspend fun getSubscriptionStatus(): AuthResult<SubscriptionInfo>
    
    suspend fun updateSubscription(tier: String, autoRenew: Boolean?): AuthResult<SubscriptionInfo>
    
    // ========== Invitations ==========
    
    suspend fun createInvitation(invitedEmail: String): AuthResult<Invitation>
    
    suspend fun getWorkshopInvitations(): AuthResult<List<Invitation>>
    
    suspend fun acceptInvitation(code: String): AuthResult<String>
    
    suspend fun getInvitationByCode(code: String): AuthResult<Invitation>
}
