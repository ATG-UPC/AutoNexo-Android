package com.atg.autonexo.features.iam.domain.repositories

import com.atg.autonexo.features.iam.domain.models.AuthResult
import com.atg.autonexo.features.iam.domain.models.User
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio de dominio para operaciones de autenticación y gestión de usuarios
 */
interface AuthRepository {
    
    // ========== Autenticación ==========
    
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
    
    // ========== Gestión de Perfil ==========
    
    /**
     * Obtiene el usuario actual desde la base de datos local
     */
    suspend fun getCurrentUser(): User?
    
    /**
     * Obtiene el perfil del usuario actual desde el backend
     */
    suspend fun refreshCurrentUser(): AuthResult<User>
    
    /**
     * Actualiza el perfil del usuario
     */
    suspend fun updateProfile(
        firstName: String,
        lastName: String,
        phoneNumber: String
    ): AuthResult<User>
    
    /**
     * Cambia la contraseña del usuario
     */
    suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): AuthResult<String>
    
    /**
     * Desactiva la cuenta del usuario
     */
    suspend fun deactivateAccount(): AuthResult<String>
    
    /**
     * Flow que emite el usuario actual cuando cambia
     */
    fun getCurrentUserFlow(): Flow<User?>
    
    /**
     * Verifica si hay un usuario autenticado
     */
    fun isAuthenticated(): Boolean
    
    // ========== Verificación de Email ==========
    
    /**
     * Reenvía el email de verificación
     */
    suspend fun resendVerificationEmail(email: String): AuthResult<String>
    
    /**
     * Verifica el email con el token
     */
    suspend fun verifyEmail(token: String): AuthResult<String>
    
    /**
     * Obtiene el estado de verificación de un email
     */
    suspend fun getVerificationStatus(email: String): AuthResult<Boolean>
    
    // ========== Recuperación de Contraseña ==========
    
    /**
     * Solicita un reset de contraseña
     */
    suspend fun requestPasswordReset(email: String): AuthResult<String>
    
    /**
     * Resetea la contraseña con el token
     */
    suspend fun resetPassword(token: String, newPassword: String): AuthResult<String>
}
