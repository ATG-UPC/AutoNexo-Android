package com.atg.autonexo.features.trust.domain.models

data class TrustScore(
    val score: Double,
    val totalReviews: Int,
    val averageRating: Double
)

