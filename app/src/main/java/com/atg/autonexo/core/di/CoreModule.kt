package com.atg.autonexo.core.di

import android.content.Context
import com.atg.autonexo.core.data.PreferencesDataStore
import com.atg.autonexo.core.data.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext context: Context
    ): PreferencesDataStore {
        return PreferencesDataStore(context)
    }

    @Provides
    @Singleton
    fun provideUserPreferences(
        preferencesDataStore: PreferencesDataStore
    ): UserPreferences {
        return UserPreferences(preferencesDataStore)
    }
}

