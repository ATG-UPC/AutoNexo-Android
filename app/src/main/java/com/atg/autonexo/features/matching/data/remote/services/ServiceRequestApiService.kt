package com.atg.autonexo.features.matching.data.remote.services

import com.atg.autonexo.features.matching.data.remote.models.ServiceRequestResponseDto
import retrofit2.Response
import retrofit2.http.GET

interface ServiceRequestApiService {

    @GET("api/service-requests")
    suspend fun getServiceRequests(): Response<List<ServiceRequestResponseDto>>
}