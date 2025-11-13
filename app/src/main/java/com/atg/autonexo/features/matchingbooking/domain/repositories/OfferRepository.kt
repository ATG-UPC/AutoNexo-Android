package com.atg.autonexo.features.matchingbooking.domain.repositories

import com.atg.autonexo.features.iam.domain.models.AuthResult
import com.atg.autonexo.features.matchingbooking.domain.models.Offer

interface OfferRepository {
    suspend fun createOffer(
        serviceRequestId: String,
        estimatedPrice: Double,
        estimatedDuration: Int,
        description: String
    ): AuthResult<Offer>
    
    suspend fun getMyWorkshopOffers(status: String?, page: Int?, size: Int?): AuthResult<List<Offer>>
    suspend fun getOfferById(offerId: String): AuthResult<Offer>
    suspend fun withdrawOffer(offerId: String): AuthResult<String>
}
