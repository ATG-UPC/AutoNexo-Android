package com.atg.autonexo.features.subscription.domain.repositories

import com.atg.autonexo.features.iam.domain.models.AuthResult
import com.atg.autonexo.features.subscription.domain.models.Payment

interface SubscriptionRepository {
    suspend fun createSubscriptionPayment(subscriptionTier: String, paymentMethod: String?): AuthResult<Payment>
    suspend fun getPaymentById(paymentId: Long): AuthResult<Payment>
    suspend fun getMyPayments(page: Int?, size: Int?): AuthResult<List<Payment>>
    suspend fun completePayment(paymentId: Long): AuthResult<Payment>
    suspend fun cancelPayment(paymentId: Long): AuthResult<String>
}
