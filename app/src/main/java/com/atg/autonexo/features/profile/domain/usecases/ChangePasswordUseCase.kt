package com.atg.autonexo.features.profile.domain.usecases

import com.atg.autonexo.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(currentPassword: String, newPassword: String): Result<String> {
        return authRepository.changePassword(currentPassword, newPassword)
    }
}

