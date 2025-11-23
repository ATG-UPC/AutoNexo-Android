package com.atg.autonexo.features.auth.domain.usecases

import com.atg.autonexo.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class ResendVerificationUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<String> {
        return repository.resendVerification(email)
    }
}

