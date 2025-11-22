package com.atg.autonexo.features.workshop.domain.models

import java.time.LocalDateTime

data class Workshop(
    val id: Long,
    val ownerUserId: Long,
    val name: String,
    val shortDescription: String?,
    val legalName: String?,
    val ruc: String?,
    val rucVerified: Boolean,
    val trustScore: Float?,
    val active: Boolean,
    val deletedAt: LocalDateTime?,
    val logoUrl: String?,
    val photoUrls: List<String>,
    val capabilityTags: List<String>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

