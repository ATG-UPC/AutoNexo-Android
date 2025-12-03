package com.atg.autonexo.features.profile.domain.usecases

import com.atg.autonexo.features.profile.domain.models.Profile
import com.atg.autonexo.features.profile.domain.repositories.ProfileRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(): Result<Profile> = repository.getProfile()
}


