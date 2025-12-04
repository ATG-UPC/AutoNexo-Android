package com.atg.autonexo.core.geocoding

import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingService {
    @GET("json")
    suspend fun reverseGeocode(
        @Query("latlng") latlng: String,
        @Query("key") key: String,
        @Query("language") language: String = "es"
    ): GeocodingResponse
}

