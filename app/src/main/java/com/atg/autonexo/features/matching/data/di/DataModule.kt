package com.atg.autonexo.features.matching.data.di

import com.atg.autonexo.features.matching.data.remote.services.OfferApiService
import com.atg.autonexo.features.matching.data.remote.services.RequestApiService
import com.atg.autonexo.features.matching.data.repositories.OfferRepositoryImpl
import com.atg.autonexo.features.matching.data.repositories.RequestRepositoryImpl
import com.atg.autonexo.features.matching.domain.repositories.OfferRepository
import com.atg.autonexo.features.matching.domain.repositories.RequestRepository
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
    fun provideOfferApiService(retrofit: Retrofit): OfferApiService {
        return retrofit.create(OfferApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOfferRepository(
        offerApiService: OfferApiService,
        gson: Gson
    ): OfferRepository {
        return OfferRepositoryImpl(offerApiService, gson)
    }

    @Provides
    @Singleton
    fun provideServiceRequestApiService(retrofit: Retrofit): RequestApiService {
        return retrofit.create(RequestApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideServiceRequestRepository(
        requestApiService: RequestApiService,
        gson: Gson
    ): RequestRepository {
        return RequestRepositoryImpl(requestApiService, gson)
    }
}