package com.atg.autonexo.features.workshop.domain.models

data class Workshop(
    val id: Long,
    val name: String,
    val description: String,
    val legalName: String?,
    val ruc: String?,
    val contactEmail: String,
    val contactPhone: String,
    val logoUrl: String?,
    val photoUrls: List<String>,
    val locations: List<Location>,
    val serviceTemplates: List<ServiceTemplate>,
    val capabilityTags: List<String>,
    val subscriptionTier: String,
    val subscriptionStatus: String,
    val trustScore: Double,
    val ownerId: Long,
    val active: Boolean,
    val createdAt: String?
)
