package com.atg.autonexo.features.iam.data.remote.services

import com.atg.autonexo.features.iam.data.remote.models.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Service Retrofit para endpoints de autenticación y gestión de usuarios
 * Base path: /api/v1/users
 */
interface AuthService {
    
    // ========== Autenticación ==========
    
    @Headers("Content-Type: application/json")
    @POST("api/v1/users/signin")
    suspend fun signIn(@Body request: LoginRequestDto): Response<LoginResponseDto>
    
    @Headers("Content-Type: application/json")
    @POST("api/v1/users/signup")
    suspend fun signUp(@Body request: SignUpRequestDto): Response<String>
    
    // ========== Perfil de Usuario ==========
    
    @GET("api/v1/users/me")
    suspend fun getCurrentUser(): Response<UserDto>
    
    @Headers("Content-Type: application/json")
    @PUT("api/v1/users/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequestDto): Response<UserDto>
    
    @Headers("Content-Type: application/json")
    @PUT("api/v1/users/me/password")
    suspend fun changePassword(@Body request: ChangePasswordRequestDto): Response<String>
    
    @DELETE("api/v1/users/me")
    suspend fun deactivateAccount(): Response<String>
    
    // ========== Verificación de Email ==========
    
    @Headers("Content-Type: application/json")
    @POST("api/v1/users/resend-verification")
    suspend fun resendVerificationEmail(@Body request: ResendVerificationRequestDto): Response<String>
    
    @Headers("Content-Type: application/json")
    @POST("api/v1/users/verify-email")
    suspend fun verifyEmail(@Body request: VerifyEmailRequestDto): Response<String>
    
    @GET("api/v1/users/verification-status")
    suspend fun getVerificationStatus(@Query("email") email: String): Response<VerificationStatusResponseDto>
    
    // ========== Recuperación de Contraseña ==========
    
    @Headers("Content-Type: application/json")
    @POST("api/v1/users/forgot-password")
    suspend fun requestPasswordReset(@Body request: ForgotPasswordRequestDto): Response<String>
    
    @Headers("Content-Type: application/json")
    @POST("api/v1/users/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequestDto): Response<String>
    
    // ========== Roles Disponibles ==========
    
    @GET("api/v1/users/available-roles")
    suspend fun getAvailableRoles(): Response<List<String>>
}
