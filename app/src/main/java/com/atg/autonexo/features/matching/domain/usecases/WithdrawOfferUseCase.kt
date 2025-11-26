package com.atg.autonexo.features.matching.domain.usecases

import com.atg.autonexo.features.matching.domain.repositories.OfferRepository
import javax.inject.Inject

class WithdrawOfferUseCase @Inject constructor(
    private val repository: OfferRepository
) {
    suspend operator fun invoke(workshopId: Long, offerId: Long) =
        repository.withdrawOffer(offerId)
}