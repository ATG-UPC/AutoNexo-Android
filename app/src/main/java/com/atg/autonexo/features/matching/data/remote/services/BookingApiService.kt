package com.atg.autonexo.features.matching.data.remote.services

import com.atg.autonexo.features.matching.data.remote.models.AcceptScheduleRequestDto
import com.atg.autonexo.features.matching.data.remote.models.BookingResponseDto
import com.atg.autonexo.features.matching.data.remote.models.CompleteBookingRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BookingApiService {
    @GET("api/service-bookings")
    suspend fun getMyBookings(): Response<List<BookingResponseDto>>
    @POST("api/service-bookings/{id}/confirm-schedule")
    suspend fun acceptScheduleChange(
        @Path("id") bookingId: Long,
        @Body request: AcceptScheduleRequestDto
    ): Response<BookingResponseDto>
    @POST("api/service-bookings/{id}/complete")
    suspend fun completeBooking(
        @Path("id") bookingId: Long,
        @Body request: CompleteBookingRequestDto
    ): Response<BookingResponseDto>
    @DELETE("api/service-bookings/{id}")
    suspend fun cancelBooking(
        @Path("id") bookingId: Long
    ): Response<Unit>
}