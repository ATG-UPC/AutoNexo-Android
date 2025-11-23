package com.atg.autonexo.features.auth.domain.usecases

import com.atg.autonexo.features.auth.domain.models.SignUpRequest
import com.atg.autonexo.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(request: SignUpRequest): Result<String> {
        return repository.signUp(request)
    }
}

