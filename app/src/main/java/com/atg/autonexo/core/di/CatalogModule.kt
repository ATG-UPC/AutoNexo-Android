package com.atg.autonexo.core.di

import com.atg.autonexo.core.data.catalog.CatalogRepository
import com.atg.autonexo.core.data.catalog.CatalogService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CatalogModule {
    
    @Provides
    @Singleton
    fun provideCatalogService(retrofit: Retrofit): CatalogService {
        return retrofit.create(CatalogService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideCatalogRepository(
        catalogService: CatalogService
    ): CatalogRepository {
        return CatalogRepository(catalogService)
    }
}

