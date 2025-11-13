package com.atg.autonexo.features.matchingbooking.data.remote.services

import com.atg.autonexo.features.matchingbooking.data.remote.models.ServiceRequestDto
import retrofit2.Response
import retrofit2.http.*

/**
 * Service Retrofit para Service Requests
 * Base path: /api/service-requests (sin v1)
 */
interface ServiceRequestService {
    
    @GET("api/service-requests/{id}")
    suspend fun getServiceRequestById(@Path("id") requestId: Long): Response<ServiceRequestDto>
    
    @POST("api/service-requests/{id}/reject")
    suspend fun rejectServiceRequest(@Path("id") requestId: Long): Response<String>
    
    @GET("api/service-requests")
    suspend fun getServiceRequests(
        @Query("status") status: String?,
        @Query("page") page: Int?,
        @Query("size") size: Int?
    ): Response<List<ServiceRequestDto>>
}
