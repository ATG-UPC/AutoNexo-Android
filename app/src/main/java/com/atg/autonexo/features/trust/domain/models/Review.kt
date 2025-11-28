package com.atg.autonexo.features.trust.domain.models

import java.time.LocalDateTime

data class Review(
    val id: Long,
    val serviceBookingId: Long,
    val reviewerId: Long,
    val revieweeUserId: Long,
    val revieweeWorkshopId: Long,
    val reviewType: ReviewType,
    val rating: Int,
    val comment: String,
    val reviewStatus: ReviewStatus,
    val submittedAt: LocalDateTime?,
    val windowExpiresAt: LocalDateTime?,
    val createdAt: LocalDateTime

)