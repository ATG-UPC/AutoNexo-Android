package com.atg.autonexo.core.navigation

import com.atg.autonexo.core.data.UserPreferences
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface UserPrefsEntryPoint {
    fun userPreferences(): UserPreferences
}


