package com.atg.autonexo.features.auth.domain.repositories

import com.atg.autonexo.features.auth.domain.models.SignUpRequest
import com.atg.autonexo.features.auth.domain.models.User

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<Pair<String, User>>
    suspend fun signUp(request: SignUpRequest): Result<String>
    suspend fun verifyEmail(token: String): Result<String>
    suspend fun resendVerification(email: String): Result<String>
    suspend fun forgotPassword(email: String): Result<String>
    suspend fun resetPassword(token: String, newPassword: String): Result<String>
    suspend fun getCurrentUser(): Result<User>
    suspend fun updateProfile(firstName: String, lastName: String, phoneNumber: String): Result<User>
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<String>
}

