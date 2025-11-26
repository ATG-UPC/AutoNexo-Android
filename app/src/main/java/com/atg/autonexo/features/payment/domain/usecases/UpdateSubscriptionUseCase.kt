package com.atg.autonexo.features.payment.domain.usecases

import com.atg.autonexo.features.payment.domain.models.Payment
import com.atg.autonexo.features.payment.domain.repositories.PaymentRepository

import javax.inject.Inject

class UpdateSubscriptionUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(payment: Payment) = repository.updateSubscription(payment)
}