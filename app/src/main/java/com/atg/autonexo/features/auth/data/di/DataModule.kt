package com.atg.autonexo.features.auth.data.di

import com.atg.autonexo.core.data.PreferencesDataStore
import com.atg.autonexo.features.auth.data.remote.services.AuthApiService
import com.atg.autonexo.features.auth.data.repositories.AuthRepositoryImpl
import com.atg.autonexo.features.auth.domain.repositories.AuthRepository
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        authApiService: AuthApiService,
        preferencesDataStore: PreferencesDataStore,
        gson: Gson
    ): AuthRepository {
        return AuthRepositoryImpl(authApiService, preferencesDataStore, gson)
    }
}

