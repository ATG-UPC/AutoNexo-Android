package com.atg.autonexo.features.profile.data.di

import com.atg.autonexo.features.auth.domain.repositories.AuthRepository
import com.atg.autonexo.features.profile.data.repositories.ProfileRepositoryImpl
import com.atg.autonexo.features.profile.domain.repositories.ProfileRepository
import com.atg.autonexo.features.workshop.domain.repositories.WorkshopRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideProfileRepository(
        authRepository: AuthRepository,
        workshopRepository: WorkshopRepository
    ): ProfileRepository {
        return ProfileRepositoryImpl(authRepository, workshopRepository)
    }
}

