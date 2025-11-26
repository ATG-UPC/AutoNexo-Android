package com.atg.autonexo.features.payment.data.di

import android.content.Context
import com.atg.autonexo.features.payment.data.remote.services.PaymentApiService
import com.atg.autonexo.features.payment.data.repositories.PaymentRepositoryImpl
import com.atg.autonexo.features.payment.domain.repositories.PaymentRepository
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
    fun providePaymentApiService(retrofit: Retrofit): PaymentApiService {
        return retrofit.create(PaymentApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePaymentRepository(
        paymentApiService: PaymentApiService,
        gson: Gson,
        @ApplicationContext context: Context
    ): PaymentRepository{
        return PaymentRepositoryImpl(paymentApiService, gson, context)
    }


}