package com.atg.autonexo.features.auth.data.remote.services

import com.atg.autonexo.features.auth.data.remote.models.ForgotPasswordRequestDto
import com.atg.autonexo.features.auth.data.remote.models.ResendVerificationRequestDto
import com.atg.autonexo.features.auth.data.remote.models.ResetPasswordRequestDto
import com.atg.autonexo.features.auth.data.remote.models.SignInRequestDto
import com.atg.autonexo.features.auth.data.remote.models.SignInResponseDto
import com.atg.autonexo.features.auth.data.remote.models.SignUpRequestDto
import com.atg.autonexo.features.auth.data.remote.models.VerifyEmailRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {
    
    @POST("api/v1/users/signin")
    suspend fun signIn(@Body request: SignInRequestDto): Response<SignInResponseDto>
    
    @POST("api/v1/users/signup")
    suspend fun signUp(@Body request: SignUpRequestDto): Response<String>
    
    @POST("api/v1/users/verify-email")
    suspend fun verifyEmail(@Body request: VerifyEmailRequestDto): Response<String>
    
    @POST("api/v1/users/resend-verification")
    suspend fun resendVerification(@Body request: ResendVerificationRequestDto): Response<String>
    
    @POST("api/v1/users/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequestDto): Response<String>
    
    @POST("api/v1/users/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequestDto): Response<String>
    
    @GET("api/v1/users/available-roles")
    suspend fun getAvailableRoles(): Response<List<String>>
    
    @GET("api/v1/users/me")
    suspend fun getCurrentUser(): Response<com.atg.autonexo.features.auth.data.remote.models.UserDto>
}

