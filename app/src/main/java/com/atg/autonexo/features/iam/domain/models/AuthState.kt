package com.atg.autonexo.features.iam.domain.models

/**
 * Representa el estado de autenticación del usuario
 */
sealed class AuthState {
    object Unauthenticated : AuthState()
    data class Authenticated(val user: User, val token: String) : AuthState()
    data class Error(val message: String) : AuthState()
}




