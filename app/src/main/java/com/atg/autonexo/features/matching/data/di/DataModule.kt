package com.atg.autonexo.features.matching.data.di

import android.content.Context
import com.atg.autonexo.features.matching.data.remote.services.BookingApiService
import com.atg.autonexo.features.matching.data.remote.services.OfferApiService
import com.atg.autonexo.features.matching.data.remote.services.RequestApiService
import com.atg.autonexo.features.matching.data.repositories.BookingRepositoryImpl
import com.atg.autonexo.features.matching.data.repositories.OfferRepositoryImpl
import com.atg.autonexo.features.matching.data.repositories.RequestRepositoryImpl
import com.atg.autonexo.features.matching.domain.repositories.BookingRepository
import com.atg.autonexo.features.matching.domain.repositories.OfferRepository
import com.atg.autonexo.features.matching.domain.repositories.RequestRepository
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Provides
    @Singleton
    fun provideBookingApiService(retrofit: Retrofit): BookingApiService {
        return retrofit.create(BookingApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideBookingRepository(
        bookingApiService: BookingApiService,
        gson: Gson
    ): BookingRepository {
        return BookingRepositoryImpl(bookingApiService, gson)
    }

}