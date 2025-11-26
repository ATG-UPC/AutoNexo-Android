package com.atg.autonexo.features.matching.domain.usecases

import com.atg.autonexo.features.matching.domain.models.Offer
import com.atg.autonexo.features.matching.domain.repositories.OfferRepository
import javax.inject.Inject

class GetMyOffersUseCase @Inject constructor(
    private val repository: OfferRepository,
) {
    suspend operator fun invoke() = repository.getMyOffers()

    suspend fun getSentOffers(): Result<List<Offer>> {
        return repository.getMyOffers().fold(
            onSuccess = { offers ->
                Result.success(offers)
            },
            onFailure = { exception ->
                Result.failure(exception)
            }
        )
    }
}