package com.atg.autonexo.features.matching.data.remote.services

import com.atg.autonexo.features.matching.data.remote.models.RequestResponseDto
import retrofit2.Response
import retrofit2.http.GET

interface RequestApiService {

    @GET("api/service-requests")
    suspend fun getServiceRequests(): Response<List<RequestResponseDto>>
}