package com.atg.autonexo.core.geocoding

import android.content.Context
import android.content.pm.PackageManager
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GeocodingModule {

    private const val GOOGLE_MAPS_GEOCODING_BASE_URL = "https://maps.googleapis.com/maps/api/geocode/"

    @Provides
    @Singleton
    @Named("GeocodingOkHttpClient")
    fun provideGeocodingOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            android.util.Log.d("GeocodingOkHttp", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("GeocodingRetrofit")
    fun provideGeocodingRetrofit(
        @Named("GeocodingOkHttpClient") okHttpClient: OkHttpClient,
        gson: Gson  // Reutilizar Gson del NetworkModule
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(GOOGLE_MAPS_GEOCODING_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideGeocodingService(
        @Named("GeocodingRetrofit") retrofit: Retrofit
    ): GeocodingService {
        return retrofit.create(GeocodingService::class.java)
    }

    @Provides
    @Singleton
    fun provideMapsApiKey(@ApplicationContext context: Context): String {
        return try {
            val appInfo = context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            )
            appInfo.metaData.getString("com.google.android.geo.API_KEY") ?: ""
        } catch (e: Exception) {
            ""
        }
    }
}

