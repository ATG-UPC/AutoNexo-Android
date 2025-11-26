package com.atg.autonexo.features.payment.domain.usecases

import com.atg.autonexo.features.payment.domain.models.CreatePaymentRequest
import com.atg.autonexo.features.payment.domain.repositories.PaymentRepository
import javax.inject.Inject

class CreateSubscriptionUseCase@Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(request: CreatePaymentRequest) = repository.createSubscription(request)
}