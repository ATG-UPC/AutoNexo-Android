package com.atg.autonexo.features.iam.domain.repositories

import com.atg.autonexo.features.iam.domain.models.AuthResult
import com.atg.autonexo.features.iam.domain.models.User
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio de dominio para operaciones de autenticación
 */
interface AuthRepository {
    
    /**
     * Inicia sesión con email y password
     * @return AuthResult con el User y token si es exitoso
     */
    suspend fun signIn(email: String, password: String): AuthResult<User>
    
    /**
     * Registra un nuevo usuario
     * @return AuthResult con mensaje de éxito o error
     */
    suspend fun signUp(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String,
        requestedRole: String,
        invitationCode: String? = null
    ): AuthResult<String>
    
    /**
     * Cierra sesión del usuario actual
     */
    suspend fun signOut()
    
    /**
     * Obtiene el usuario actual desde la base de datos local
     */
    suspend fun getCurrentUser(): User?
    
    /**
     * Flow que emite el usuario actual cuando cambia
     */
    fun getCurrentUserFlow(): Flow<User?>
    
    /**
     * Verifica si hay un usuario autenticado
     */
    fun isAuthenticated(): Boolean
}
