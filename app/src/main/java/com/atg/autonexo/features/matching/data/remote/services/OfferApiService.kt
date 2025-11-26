package com.atg.autonexo.features.matching.data.remote.services

import com.atg.autonexo.features.matching.data.remote.models.CreateOfferRequestDto
import com.atg.autonexo.features.matching.data.remote.models.OfferResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OfferApiService {

    // Recibir mis Offers
    @GET("api/offers/my-requests")
    suspend fun getMyOffers(): Response<List<OfferResponseDto>>

    // Crear Offer
    @POST("api/offers")
    suspend fun createOffer(@Body request: CreateOfferRequestDto): Response<OfferResponseDto>

    // Cancelar creo q es Withdraw
    @DELETE("api/offers/{id}")
    suspend fun withdrawOffer(@Path("id") offerId: Long): Response<Unit>

}