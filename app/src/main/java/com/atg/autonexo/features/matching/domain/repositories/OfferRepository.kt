package com.atg.autonexo.features.matching.domain.repositories

import com.atg.autonexo.features.matching.domain.models.CreateOfferRequest
import com.atg.autonexo.features.matching.domain.models.Offer

interface OfferRepository {
    suspend fun createOffer(request: CreateOfferRequest): Result<Offer>
    suspend fun withdrawOffer(offerId: Long): Result<Unit>
    suspend fun getMyOffers(): Result<List<Offer>>
}