package com.atg.autonexo.features.trust.data.remote.services

import com.atg.autonexo.features.trust.data.remote.models.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Service Retrofit para Trust & Reputation
 * Base path: /api (sin v1)
 */
interface TrustService {
    
    // ========== Reviews ==========
    
    @Headers("Content-Type: application/json")
    @POST("api/reviews")
    suspend fun createReview(@Body request: CreateReviewRequestDto): Response<ReviewDto>
    
    @GET("api/reviews/service-bookings/{serviceBookingId}")
    suspend fun getReviewsForBooking(@Path("serviceBookingId") serviceBookingId: Long): Response<List<ReviewDto>>
    
    @GET("api/reviews/received/workshops/{workshopId}")
    suspend fun getReceivedReviewsForWorkshop(
        @Path("workshopId") workshopId: Long,
        @Query("status") status: String?,
        @Query("page") page: Int?,
        @Query("size") size: Int?
    ): Response<List<ReviewDto>>
    
    @GET("api/reviews/window-status")
    suspend fun getReviewWindowStatus(@Query("serviceBookingId") serviceBookingId: Long): Response<ReviewWindowStatusDto>
    
    @POST("api/reviews/{reviewId}/report")
    suspend fun reportReview(
        @Path("reviewId") reviewId: Long,
        @Query("reason") reason: String
    ): Response<String>
    
    // ========== Trust Scores ==========
    
    @GET("api/trust-score/workshops/{workshopId}")
    suspend fun getWorkshopTrustScore(@Path("workshopId") workshopId: Long): Response<TrustScoreDto>
    
    @GET("api/trust-score/my-score")
    suspend fun getMyTrustScore(): Response<TrustScoreDto>
}
