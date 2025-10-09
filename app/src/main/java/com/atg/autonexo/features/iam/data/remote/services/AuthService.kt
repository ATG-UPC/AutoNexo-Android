package com.atg.autonexo.features.iam.data.remote.services

import com.atg.autonexo.features.iam.data.remote.models.LoginRequestDto
import com.atg.autonexo.features.iam.data.remote.models.LoginResponseDto
import com.atg.autonexo.features.iam.data.remote.models.SignUpRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Service Retrofit para endpoints de autenticación
 * Base path: /api/v1/users
 */
interface AuthService {
    
    @Headers("Content-Type: application/json")
    @POST("api/v1/users/signin")
    suspend fun signIn(@Body request: LoginRequestDto): Response<LoginResponseDto>
    
    @Headers("Content-Type: application/json")
    @POST("api/v1/users/signup")
    suspend fun signUp(@Body request: SignUpRequestDto): Response<String>
}
