package com.atg.autonexo.features.auth.domain.usecases

import com.atg.autonexo.features.auth.domain.models.User
import com.atg.autonexo.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<User> {
        return repository.getCurrentUser()
    }
}

