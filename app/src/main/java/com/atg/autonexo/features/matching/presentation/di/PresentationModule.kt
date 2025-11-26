package com.atg.autonexo.features.matching.presentation.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object PresentationModule {
    // Los ViewModels se inyectan automáticamente con @HiltViewModel
}

