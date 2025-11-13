package com.atg.autonexo.features.matchingbooking.data.remote.services

import com.atg.autonexo.features.matchingbooking.data.remote.models.BookingDto
import com.atg.autonexo.features.matchingbooking.data.remote.models.CompleteServiceRequestDto
import retrofit2.Response
import retrofit2.http.*

/**
 * Service Retrofit para Service Bookings
 * Base path: /api/service-bookings (sin v1)
 */
interface BookingService {
    
    @GET("api/service-bookings")
    suspend fun getWorkshopBookings(
        @Query("status") status: String?,
        @Query("upcoming") upcoming: Boolean?,
        @Query("page") page: Int?,
        @Query("size") size: Int?
    ): Response<List<BookingDto>>
    
    @GET("api/service-bookings/{id}")
    suspend fun getBookingById(@Path("id") bookingId: String): Response<BookingDto>
    
    @POST("api/service-bookings/{id}/confirm-schedule")
    suspend fun confirmSchedule(@Path("id") bookingId: String): Response<BookingDto>
    
    @Headers("Content-Type: application/json")
    @POST("api/service-bookings/{id}/propose-change")
    suspend fun proposeScheduleChange(
        @Path("id") bookingId: String,
        @Body proposedDateTime: String
    ): Response<BookingDto>
    
    @Headers("Content-Type: application/json")
    @POST("api/service-bookings/{id}/complete")
    suspend fun completeService(
        @Path("id") bookingId: String,
        @Body request: CompleteServiceRequestDto?
    ): Response<BookingDto>
    
    @DELETE("api/service-bookings/{id}")
    suspend fun cancelBooking(@Path("id") bookingId: String): Response<String>
}
