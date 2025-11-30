package com.atg.autonexo.features.matching.data.remote.services

import com.atg.autonexo.features.matching.data.remote.models.AcceptScheduleChangeRequestDto
import com.atg.autonexo.features.matching.data.remote.models.BookingResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface BookingApiService {
    @GET("api/service-bookings")
    suspend fun getMyBookings(): Response<List<BookingResponseDto>>

    @PUT("api/service-bookings/{bookingId}/accept-schedule-change")
    suspend fun acceptScheduleChange(
        @Path("bookingId") bookingId: Long,
        @Body request: AcceptScheduleChangeRequestDto
    ): Response<Unit>
}