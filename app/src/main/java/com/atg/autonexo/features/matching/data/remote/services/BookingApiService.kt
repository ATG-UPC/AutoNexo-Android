package com.atg.autonexo.features.matching.data.remote.services

import com.atg.autonexo.features.matching.data.remote.models.BookingResponseDto
import retrofit2.Response
import retrofit2.http.GET

interface BookingApiService {
    @GET("api/service-bookings")
    suspend fun getMyBookings(): Response<List<BookingResponseDto>>
}