package com.atg.autonexo.features.auth.domain.usecases

import com.atg.autonexo.features.auth.domain.models.User
import com.atg.autonexo.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Pair<String, User>> {
        return repository.signIn(email, password)
    }
}

