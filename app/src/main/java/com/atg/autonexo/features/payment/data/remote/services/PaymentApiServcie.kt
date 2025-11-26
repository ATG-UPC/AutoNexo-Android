package com.atg.autonexo.features.payment.data.remote.services

import com.atg.autonexo.features.payment.data.remote.models.CreatePaymentRequestDto
import com.atg.autonexo.features.payment.data.remote.models.PaymentResponseDto
import com.atg.autonexo.features.payment.data.remote.models.UpdatePaymentRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface PaymentApiService {

    @POST("api/v1/payments/subscriptions")
    suspend fun createSubscription(@Body request: CreatePaymentRequestDto): Response<Void>

    @GET("api/v1/workshops/my-workshop/subscription")
    suspend fun getMySubscription(): Response<PaymentResponseDto>

    @PUT("api/v1/workshops/my-workshop/subscription")
    suspend fun updateSubscription(@Body request: UpdatePaymentRequestDto): Response<PaymentResponseDto>
}
