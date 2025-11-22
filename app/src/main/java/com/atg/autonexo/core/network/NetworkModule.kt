package com.atg.autonexo.core.network

import com.atg.autonexo.core.data.PreferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://autonexo-backend-akcsb5avacemdwh7.canadacentral-01.azurewebsites.net/"

    @Provides
    @Singleton
    fun provideOkHttpClient(
        preferencesDataStore: PreferencesDataStore
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            android.util.Log.d("OkHttp", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
            
            // Agregar token si existe (usando runBlocking para obtener el valor del Flow)
            val token = runBlocking {
                preferencesDataStore.token.first()
            }
            
            // Log de la URL completa
            android.util.Log.d("NetworkModule", "=== REQUEST ===")
            android.util.Log.d("NetworkModule", "URL: ${originalRequest.url}")
            android.util.Log.d("NetworkModule", "Method: ${originalRequest.method}")
            
            token?.let {
                requestBuilder.addHeader("Authorization", "Bearer $it")
                android.util.Log.d("NetworkModule", "Token agregado: ${it.take(20)}...")
            }
            
            requestBuilder.addHeader("Content-Type", "application/json; charset=utf-8")
            requestBuilder.addHeader("Accept", "application/json")
            
            val request = requestBuilder.build()
            
            // Log de headers
            android.util.Log.d("NetworkModule", "Headers:")
            request.headers.forEach { header ->
                android.util.Log.d("NetworkModule", "  ${header.first}: ${header.second}")
            }
            
            val response = chain.proceed(request)
            
            android.util.Log.d("NetworkModule", "=== RESPONSE ===")
            android.util.Log.d("NetworkModule", "Response Code: ${response.code}")
            
            response
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .serializeNulls() // Serializar campos null - incluirlos en el JSON como null (requerido por el backend)
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}

