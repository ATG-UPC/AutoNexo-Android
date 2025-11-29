package com.atg.autonexo.features.profile.domain.repositories

import com.atg.autonexo.features.profile.domain.models.Profile

interface ProfileRepository {
    suspend fun getProfile(): Result<Profile>
}


