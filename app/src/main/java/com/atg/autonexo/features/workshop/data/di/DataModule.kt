package com.atg.autonexo.features.workshop.data.di

import android.content.Context
import com.atg.autonexo.features.workshop.data.remote.services.CatalogApiService
import com.atg.autonexo.features.workshop.data.remote.services.WorkshopApiService
import com.atg.autonexo.features.workshop.data.repositories.CatalogRepositoryImpl
import com.atg.autonexo.features.workshop.data.repositories.WorkshopRepositoryImpl
import com.atg.autonexo.features.workshop.domain.repositories.CatalogRepository
import com.atg.autonexo.features.workshop.domain.repositories.WorkshopRepository
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
    fun provideWorkshopApiService(retrofit: Retrofit): WorkshopApiService {
        return retrofit.create(WorkshopApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCatalogApiService(retrofit: Retrofit): CatalogApiService {
        return retrofit.create(CatalogApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideWorkshopRepository(
        workshopApiService: WorkshopApiService,
        gson: Gson,
        @ApplicationContext context: Context
    ): WorkshopRepository {
        return WorkshopRepositoryImpl(workshopApiService, gson, context)
    }

    @Provides
    @Singleton
    fun provideCatalogRepository(
        catalogApiService: CatalogApiService,
        gson: Gson
    ): CatalogRepository {
        return CatalogRepositoryImpl(catalogApiService, gson)
    }
}

