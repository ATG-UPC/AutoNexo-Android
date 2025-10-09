package com.atg.autonexo.features.iam.domain.models

/**
 * Resultado de operaciones de autenticación
 */
sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val message: String, val code: Int? = null) : AuthResult<Nothing>()
    object Loading : AuthResult<Nothing>()
}




