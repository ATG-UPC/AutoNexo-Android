package com.atg.autonexo.features.matchingbooking.data.remote.services

import com.atg.autonexo.features.matchingbooking.data.remote.models.CreateOfferRequestDto
import com.atg.autonexo.features.matchingbooking.data.remote.models.OfferDto
import retrofit2.Response
import retrofit2.http.*

/**
 * Service Retrofit para Offers
 * Base path: /api/offers
 */
interface OfferService {
    
    @Headers("Content-Type: application/json")
    @POST("api/offers")
    suspend fun createOffer(@Body request: CreateOfferRequestDto): Response<OfferDto>
    
    @GET("api/offers/my-workshop")
    suspend fun getMyWorkshopOffers(
        @Query("status") status: String?,
        @Query("page") page: Int?,
        @Query("size") size: Int?
    ): Response<List<OfferDto>>
    
    @GET("api/offers/service-requests/{requestId}")
    suspend fun getOffersByServiceRequest(@Path("requestId") requestId: Long): Response<List<OfferDto>>
    
    @GET("api/offers/{id}")
    suspend fun getOfferById(@Path("id") offerId: Long): Response<OfferDto>
    
    @DELETE("api/offers/{id}")
    suspend fun withdrawOffer(@Path("id") offerId: Long): Response<String>
}
