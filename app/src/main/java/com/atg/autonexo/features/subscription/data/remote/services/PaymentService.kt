package com.atg.autonexo.features.subscription.data.remote.services

import com.atg.autonexo.features.subscription.data.remote.models.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Service Retrofit para Payment Context
 * Base path: /api/v1/payments y /api/v1/billing
 */
interface PaymentService {
    
    @Headers("Content-Type: application/json")
    @POST("api/v1/payments/subscriptions")
    suspend fun createSubscriptionPayment(@Body request: CreatePaymentRequestDto): Response<PaymentDto>
    
    @GET("api/v1/payments/{id}")
    suspend fun getPaymentById(@Path("id") paymentId: Long): Response<PaymentDto>
    
    @GET("api/v1/payments/my-payments")
    suspend fun getMyPayments(
        @Query("page") page: Int?,
        @Query("size") size: Int?
    ): Response<List<PaymentDto>>
    
    @POST("api/v1/payments/{id}/complete")
    suspend fun completePayment(@Path("id") paymentId: Long): Response<PaymentDto>
    
    @POST("api/v1/payments/{id}/cancel")
    suspend fun cancelPayment(@Path("id") paymentId: Long): Response<String>
    
    @GET("api/v1/billing/upcoming-renewals")
    suspend fun getUpcomingRenewals(@Query("daysAhead") daysAhead: Int?): Response<List<PaymentDto>>
    
    @GET("api/v1/billing/history")
    suspend fun getBillingHistory(
        @Query("page") page: Int?,
        @Query("size") size: Int?
    ): Response<BillingHistoryDto>
}

