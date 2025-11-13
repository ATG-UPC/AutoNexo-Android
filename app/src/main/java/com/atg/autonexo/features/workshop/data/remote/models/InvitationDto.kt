package com.atg.autonexo.features.workshop.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * DTO para invitación a staff member
 */
data class InvitationDto(
    @SerializedName("id")
    val id: Long?,
    
    @SerializedName("invitationCode")
    val invitationCode: String?,
    
    @SerializedName("workshopId")
    val workshopId: Long?,
    
    @SerializedName("workshopName")
    val workshopName: String?,
    
    @SerializedName("invitedEmail")
    val invitedEmail: String?,
    
    @SerializedName("status")
    val status: String?,
    
    @SerializedName("expiresAt")
    val expiresAt: String?,
    
    @SerializedName("createdAt")
    val createdAt: String?
)

