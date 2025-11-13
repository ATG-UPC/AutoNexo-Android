package com.atg.autonexo.features.trust.domain.repositories

import com.atg.autonexo.features.iam.domain.models.AuthResult
import com.atg.autonexo.features.trust.domain.models.Review
import com.atg.autonexo.features.trust.domain.models.TrustScore

interface TrustRepository {
    suspend fun createReview(serviceBookingId: Long, rating: Int, comment: String?): AuthResult<Review>
    suspend fun getReviewsForBooking(serviceBookingId: Long): AuthResult<List<Review>>
    suspend fun getReceivedReviewsForWorkshop(workshopId: Long, status: String?, page: Int?, size: Int?): AuthResult<List<Review>>
    suspend fun getWorkshopTrustScore(workshopId: Long): AuthResult<TrustScore>
    suspend fun getMyTrustScore(): AuthResult<TrustScore>
}
