package com.atg.autonexo.features.workshop.data.remote.services

import com.atg.autonexo.features.workshop.data.remote.models.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Service Retrofit para endpoints del Workshop Context
 * Base path: /api/v1/workshops y /api/v1/invitations
 */
interface WorkshopService {
    
    // ========== Workshop Management ==========
    
    @Headers("Content-Type: application/json")
    @POST("api/v1/workshops")
    suspend fun createWorkshop(@Body request: CreateWorkshopRequestDto): Response<WorkshopDto>
    
    @GET("api/v1/workshops/my-workshop")
    suspend fun getMyWorkshop(): Response<WorkshopDto>
    
    @GET("api/v1/workshops/{id}")
    suspend fun getWorkshopById(@Path("id") workshopId: Long): Response<WorkshopDto>
    
    @Headers("Content-Type: application/json")
    @PUT("api/v1/workshops")
    suspend fun updateWorkshop(@Body request: UpdateWorkshopRequestDto): Response<WorkshopDto>
    
    // ========== Location Management ==========
    
    @Headers("Content-Type: application/json")
    @POST("api/v1/workshops/locations")
    suspend fun addLocation(@Body request: CreateLocationRequestDto): Response<LocationDto>
    
    @GET("api/v1/workshops/my-workshop/locations")
    suspend fun getMyWorkshopLocations(): Response<List<LocationDto>>
    
    @GET("api/v1/workshops/my-workshop/locations/{id}")
    suspend fun getLocationById(@Path("id") locationId: Long): Response<LocationDto>
    
    @Headers("Content-Type: application/json")
    @PUT("api/v1/workshops/my-workshop/locations/{id}")
    suspend fun updateLocation(
        @Path("id") locationId: Long,
        @Body request: CreateLocationRequestDto
    ): Response<LocationDto>
    
    @DELETE("api/v1/workshops/my-workshop/locations/{id}")
    suspend fun deleteLocation(@Path("id") locationId: Long): Response<String>
    
    // ========== Service Templates ==========
    
    @Headers("Content-Type: application/json")
    @POST("api/v1/workshops/service-templates")
    suspend fun addServiceTemplate(@Body request: CreateServiceTemplateRequestDto): Response<ServiceTemplateDto>
    
    // ========== Capability Tags ==========
    
    @POST("api/v1/workshops/tags")
    suspend fun addCapabilityTag(@Query("tag") tag: String): Response<String>
    
    @Headers("Content-Type: application/json")
    @PUT("api/v1/workshops/tags")
    suspend fun updateCapabilityTags(@Body tags: List<String>): Response<String>
    
    // ========== Media Management ==========
    
    @Multipart
    @POST("api/v1/workshops/logo")
    suspend fun uploadLogo(@Part logo: MultipartBody.Part): Response<String>
    
    @Multipart
    @POST("api/v1/workshops/photos")
    suspend fun addPhoto(@Part photo: MultipartBody.Part): Response<String>
    
    @DELETE("api/v1/workshops/photos/{photoIndex}")
    suspend fun deletePhoto(@Path("photoIndex") photoIndex: Int): Response<String>
    
    // ========== Subscription Management ==========
    
    @GET("api/v1/workshops/my-workshop/subscription")
    suspend fun getSubscriptionStatus(): Response<SubscriptionDto>
    
    @Headers("Content-Type: application/json")
    @PUT("api/v1/workshops/my-workshop/subscription")
    suspend fun updateSubscription(@Body request: UpdateSubscriptionRequestDto): Response<SubscriptionDto>
    
    // ========== Invitation Management ==========
    
    @Headers("Content-Type: application/json")
    @POST("api/v1/invitations")
    suspend fun createInvitation(@Body request: CreateInvitationRequestDto): Response<InvitationDto>
    
    @GET("api/v1/invitations")
    suspend fun getWorkshopInvitations(): Response<List<InvitationDto>>
    
    @Headers("Content-Type: application/json")
    @POST("api/v1/invitations/accept")
    suspend fun acceptInvitation(@Query("code") code: String): Response<String>
    
    @GET("api/v1/invitations/{code}")
    suspend fun getInvitationByCode(@Path("code") code: String): Response<InvitationDto>
    
    // ========== Public Workshop Search (for reference) ==========
    
    @GET("api/v1/workshops/search")
    suspend fun searchWorkshops(
        @Query("latitude") latitude: Double?,
        @Query("longitude") longitude: Double?,
        @Query("radiusKm") radiusKm: Double?,
        @Query("services") services: String?,
        @Query("tags") tags: String?,
        @Query("minRating") minRating: Double?
    ): Response<List<WorkshopDto>>
    
    @GET("api/v1/workshops/{id}/public")
    suspend fun getPublicWorkshopInfo(@Path("id") workshopId: Long): Response<WorkshopDto>
    
    @GET("api/v1/workshops/{id}/services")
    suspend fun getWorkshopServices(@Path("id") workshopId: Long): Response<List<ServiceTemplateDto>>
}
