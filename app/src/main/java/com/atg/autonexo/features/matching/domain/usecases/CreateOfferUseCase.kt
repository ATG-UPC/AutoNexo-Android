package com.atg.autonexo.features.matching.domain.usecases

import com.atg.autonexo.features.matching.domain.models.CreateOfferRequest
import com.atg.autonexo.features.matching.domain.repositories.OfferRepository
import javax.inject.Inject

class CreateOfferUseCase @Inject constructor(
    private val repository: OfferRepository
) {
    suspend operator fun invoke(request: CreateOfferRequest) = repository.createOffer(request)
}