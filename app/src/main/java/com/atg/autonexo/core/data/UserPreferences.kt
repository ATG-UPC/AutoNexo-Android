package com.atg.autonexo.core.data

import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    private val preferencesDataStore: PreferencesDataStore
) {
    suspend fun isLoggedIn(): Boolean {
        return preferencesDataStore.token.first() != null
    }

    suspend fun getToken(): String? {
        return preferencesDataStore.token.first()
    }

    suspend fun getUserId(): String? {
        return preferencesDataStore.userId.first()
    }

    suspend fun getUserEmail(): String? {
        return preferencesDataStore.userEmail.first()
    }
}

