package com.atg.autonexo.features.trust.domain.models

data class Review(
    val id: Long,
    val serviceBookingId: Long,
    val reviewerId: Long,
    val reviewerName: String,
    val revieweeId: Long,
    val revieweeName: String,
    val reviewType: String,
    val rating: Int,
    val comment: String?,
    val status: String,
    val createdAt: String?
)
