package com.atg.autonexo.features.payment.domain.usecases

import com.atg.autonexo.features.payment.domain.repositories.PaymentRepository
import javax.inject.Inject

class GetMySubscriptionUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke() = repository.getMySubscription()
}