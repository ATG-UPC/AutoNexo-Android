package com.atg.autonexo.features.workshop.data.remote.services

import com.atg.autonexo.features.workshop.data.remote.models.AcceptInvitationRequestDto
import com.atg.autonexo.features.workshop.data.remote.models.AddLocationRequestDto
import com.atg.autonexo.features.workshop.data.remote.models.CreateInvitationRequestDto
import com.atg.autonexo.features.workshop.data.remote.models.CreateWorkshopRequestDto
import com.atg.autonexo.features.workshop.data.remote.models.InvitationResponseDto
import com.atg.autonexo.features.workshop.data.remote.models.LocationResponseDto
import com.atg.autonexo.features.workshop.data.remote.models.UpdateLocationRequestDto
import com.atg.autonexo.features.workshop.data.remote.models.UpdateTagsRequestDto
import com.atg.autonexo.features.workshop.data.remote.models.UpdateWorkshopRequestDto
import com.atg.autonexo.features.workshop.data.remote.models.UploadResponseDto
import com.atg.autonexo.features.workshop.data.remote.models.WorkshopEmployeeResponseDto
import com.atg.autonexo.features.workshop.data.remote.models.WorkshopResponseDto
import com.atg.autonexo.features.workshop.data.remote.models.WorkshopStaffResponseDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface WorkshopApiService {
    
    // Crear workshop
    @POST("api/v1/workshops")
    suspend fun createWorkshop(@Body request: CreateWorkshopRequestDto): Response<WorkshopResponseDto>
    
    // Obtener workshop por ID
    @GET("api/v1/workshops/{workshopId}")
    suspend fun getWorkshopById(@Path("workshopId") workshopId: Long): Response<WorkshopResponseDto>
    
    // Obtener workshop del usuario actual
    @GET("api/v1/workshops/my-workshop")
    suspend fun getMyWorkshop(): Response<WorkshopResponseDto>
    
    // Actualizar workshop
    @PUT("api/v1/workshops")
    suspend fun updateWorkshop(@Body request: UpdateWorkshopRequestDto): Response<WorkshopResponseDto>
    
    // Actualizar tags del workshop
    @PUT("api/v1/workshops/tags")
    suspend fun updateTags(@Body tags: UpdateTagsRequestDto): Response<WorkshopResponseDto>
    
    // Agregar tags al workshop
    @POST("api/v1/workshops/tags")
    suspend fun addTags(@Body tags: List<String>): Response<WorkshopResponseDto>
    
    // Subir logo
    @Multipart
    @POST("api/v1/workshops/logo")
    suspend fun uploadLogo(@Part file: MultipartBody.Part): Response<UploadResponseDto>
    
    // Subir foto
    @Multipart
    @POST("api/v1/workshops/photos")
    suspend fun uploadPhoto(@Part file: MultipartBody.Part): Response<UploadResponseDto>
    
    // Eliminar foto
    @DELETE("api/v1/workshops/photos/{photoIndex}")
    suspend fun deletePhoto(@Path("photoIndex") photoIndex: Int): Response<Unit>
    
    // Agregar ubicación
    @POST("api/v1/workshops/locations")
    suspend fun addLocation(@Body request: AddLocationRequestDto): Response<LocationResponseDto>
    
    // Obtener ubicaciones del workshop
    @GET("api/v1/workshops/my-workshop/locations")
    suspend fun getWorkshopLocations(): Response<List<LocationResponseDto>>
    
    // Obtener ubicación específica
    @GET("api/v1/workshops/my-workshop/locations/{id}")
    suspend fun getLocationById(@Path("id") locationId: Long): Response<LocationResponseDto>
    
    // Actualizar ubicación
    @PUT("api/v1/workshops/my-workshop/locations/{id}")
    suspend fun updateLocation(
        @Path("id") locationId: Long,
        @Body request: UpdateLocationRequestDto
    ): Response<LocationResponseDto>
    
    // Eliminar ubicación
    @DELETE("api/v1/workshops/my-workshop/locations/{id}")
    suspend fun deleteLocation(@Path("id") locationId: Long): Response<Unit>
    
    // Buscar workshops por tag
    @GET("api/v1/workshops/by-tag")
    suspend fun getWorkshopsByTag(@Query("tag") tag: String): Response<List<WorkshopResponseDto>>
    
    // Obtener workshops por owner
    @GET("api/v1/workshops/by-owner/{ownerUserId}")
    suspend fun getWorkshopsByOwner(@Path("ownerUserId") ownerUserId: Long): Response<List<WorkshopResponseDto>>
    
    // ========== INVITACIONES ==========
    
    // Crear invitación
    @POST("api/v1/invitations")
    suspend fun createInvitation(@Body request: CreateInvitationRequestDto): Response<InvitationResponseDto>
    
    // Obtener invitaciones del workshop
    @GET("api/v1/invitations")
    suspend fun getInvitations(): Response<List<InvitationResponseDto>>
    
    // Obtener invitación por código
    @GET("api/v1/invitations/{code}")
    suspend fun getInvitationByCode(@Path("code") code: String): Response<InvitationResponseDto>
    
    // Aceptar invitación
    @POST("api/v1/invitations/accept")
    suspend fun acceptInvitation(@Body request: AcceptInvitationRequestDto): Response<String>
    
    // ========== EMPLEADOS ==========
    
    // Obtener empleados del workshop
    @GET("api/v1/workshops/{workshopId}/employees")
    suspend fun getWorkshopEmployees(@Path("workshopId") workshopId: Long): Response<List<WorkshopEmployeeResponseDto>>
    
    // Desactivar empleado
    @PUT("api/v1/workshops/{workshopId}/employees/{employeeId}/deactivate")
    suspend fun deactivateEmployee(
        @Path("workshopId") workshopId: Long,
        @Path("employeeId") employeeId: Long
    ): Response<Unit>
    
    // Activar empleado
    @PUT("api/v1/workshops/{workshopId}/employees/{employeeId}/activate")
    suspend fun activateEmployee(
        @Path("workshopId") workshopId: Long,
        @Path("employeeId") employeeId: Long
    ): Response<Unit>

    // STAFF
    @GET("/api/v1/workshops/my-workshop/staff")
    suspend fun getWorkshopStaff(): Response<List<WorkshopStaffResponseDto>>

}

