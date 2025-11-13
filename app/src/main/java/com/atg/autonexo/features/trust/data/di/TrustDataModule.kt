package com.atg.autonexo.features.trust.data.di

import com.atg.autonexo.features.trust.data.remote.services.TrustService
import com.atg.autonexo.features.trust.data.repositories.TrustRepositoryImpl
import com.atg.autonexo.features.trust.domain.repositories.TrustRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TrustDataModule {
    
    @Provides
    @Singleton
    fun provideTrustService(retrofit: Retrofit): TrustService {
        return retrofit.create(TrustService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideTrustRepository(
        trustService: TrustService
    ): TrustRepository {
        return TrustRepositoryImpl(trustService)
    }
}

