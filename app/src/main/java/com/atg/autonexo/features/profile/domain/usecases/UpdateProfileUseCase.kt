package com.atg.autonexo.features.profile.domain.usecases

import com.atg.autonexo.features.auth.domain.models.User
import com.atg.autonexo.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(firstName: String, lastName: String, phoneNumber: String): Result<User> {
        return authRepository.updateProfile(firstName, lastName, phoneNumber)
    }
}

