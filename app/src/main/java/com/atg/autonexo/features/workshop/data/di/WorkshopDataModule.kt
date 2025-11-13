package com.atg.autonexo.features.workshop.data.di

import com.atg.autonexo.features.workshop.data.remote.services.WorkshopService
import com.atg.autonexo.features.workshop.data.repositories.WorkshopRepositoryImpl
import com.atg.autonexo.features.workshop.domain.repositories.WorkshopRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkshopDataModule {
    
    @Provides
    @Singleton
    fun provideWorkshopService(retrofit: Retrofit): WorkshopService {
        return retrofit.create(WorkshopService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideWorkshopRepository(
        workshopService: WorkshopService
    ): WorkshopRepository {
        return WorkshopRepositoryImpl(workshopService)
    }
}

