package com.atg.autonexo.core.network

import android.util.Log
import com.atg.autonexo.core.data.UserPreferences
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

/**
 * Interceptor que agrega automáticamente el JWT token a las peticiones autenticadas.
 * Lee el token de UserPreferences y lo agrega como header Authorization: Bearer <token>
 */
class AuthInterceptor @Inject constructor(
    private val userPreferences: UserPreferences
) : Interceptor {
    
    companion object {
        private const val TAG = "AuthInterceptor"
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val TOKEN_PREFIX = "Bearer "
    }
    
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Obtener el token de UserPreferences
        val token = userPreferences.getAuthToken()
        
        // Si no hay token, continuar con la petición original (endpoints públicos)
        if (token.isNullOrBlank()) {
            Log.d(TAG, "No token available, proceeding without authentication")
            return chain.proceed(originalRequest)
        }
        
        // Agregar el header de autorización
        val authenticatedRequest = originalRequest.newBuilder()
            .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + token)
            .build()
        
        Log.d(TAG, "Added Authorization header to: ${originalRequest.url}")
        
        return chain.proceed(authenticatedRequest)
    }
}

