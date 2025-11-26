package com.atg.autonexo.features.payment.domain.repositories

import com.atg.autonexo.features.payment.domain.models.CreatePaymentRequest
import com.atg.autonexo.features.payment.domain.models.Payment

interface PaymentRepository {
    suspend fun createSubscription(request: CreatePaymentRequest): Result<Payment>

    suspend fun updateSubscription(payment: Payment): Result<Payment>

    suspend fun getMySubscription(): Result<Payment>
}