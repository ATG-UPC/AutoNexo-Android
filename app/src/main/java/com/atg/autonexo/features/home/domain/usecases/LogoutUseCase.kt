package com.atg.autonexo.features.home.domain.usecases

import com.atg.autonexo.core.data.PreferencesDataStore
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val preferencesDataStore: PreferencesDataStore
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            preferencesDataStore.clearAuthData()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

