package com.atg.autonexo.features.matchingbooking.data.di

import com.atg.autonexo.features.matchingbooking.data.remote.services.BookingService
import com.atg.autonexo.features.matchingbooking.data.remote.services.OfferService
import com.atg.autonexo.features.matchingbooking.data.remote.services.ServiceRequestService
import com.atg.autonexo.features.matchingbooking.data.repositories.BookingRepositoryImpl
import com.atg.autonexo.features.matchingbooking.data.repositories.OfferRepositoryImpl
import com.atg.autonexo.features.matchingbooking.data.repositories.ServiceRequestRepositoryImpl
import com.atg.autonexo.features.matchingbooking.domain.repositories.BookingRepository
import com.atg.autonexo.features.matchingbooking.domain.repositories.OfferRepository
import com.atg.autonexo.features.matchingbooking.domain.repositories.ServiceRequestRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MatchingBookingDataModule {
    
    @Provides
    @Singleton
    fun provideServiceRequestService(retrofit: Retrofit): ServiceRequestService {
        return retrofit.create(ServiceRequestService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideOfferService(retrofit: Retrofit): OfferService {
        return retrofit.create(OfferService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideBookingService(retrofit: Retrofit): BookingService {
        return retrofit.create(BookingService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideServiceRequestRepository(
        serviceRequestService: ServiceRequestService
    ): ServiceRequestRepository {
        return ServiceRequestRepositoryImpl(serviceRequestService)
    }
    
    @Provides
    @Singleton
    fun provideOfferRepository(
        offerService: OfferService
    ): OfferRepository {
        return OfferRepositoryImpl(offerService)
    }
    
    @Provides
    @Singleton
    fun provideBookingRepository(
        bookingService: BookingService
    ): BookingRepository {
        return BookingRepositoryImpl(bookingService)
    }
}

