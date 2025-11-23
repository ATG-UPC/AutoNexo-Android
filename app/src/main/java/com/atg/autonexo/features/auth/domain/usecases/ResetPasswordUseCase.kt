package com.atg.autonexo.features.auth.domain.usecases

import com.atg.autonexo.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(token: String, newPassword: String): Result<String> {
        return repository.resetPassword(token, newPassword)
    }
}

