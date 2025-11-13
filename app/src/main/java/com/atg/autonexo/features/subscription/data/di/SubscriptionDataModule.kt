package com.atg.autonexo.features.subscription.data.di

import com.atg.autonexo.features.subscription.data.remote.services.PaymentService
import com.atg.autonexo.features.subscription.data.repositories.SubscriptionRepositoryImpl
import com.atg.autonexo.features.subscription.domain.repositories.SubscriptionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SubscriptionDataModule {
    
    @Provides
    @Singleton
    fun providePaymentService(retrofit: Retrofit): PaymentService {
        return retrofit.create(PaymentService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideSubscriptionRepository(
        paymentService: PaymentService
    ): SubscriptionRepository {
        return SubscriptionRepositoryImpl(paymentService)
    }
}

